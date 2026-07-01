import { useEffect, useMemo, useState } from "react";
import api from "../services/api";

import { Button } from "../components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "../components/ui/dialog";

import { LogOut, Pencil, Droplet, HeartPulse, Clock3 } from "lucide-react";

import { toast } from "sonner";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import { Badge } from "../components/ui/badge";
import { useNavigate } from "react-router-dom";

const COLORS = {
  crimson: "#C81E3A",
  crimsonDark: "#9F1239",
  navy: "#1B2A4A",
  ink: "#14171F",
  paper: "#FAF9F6",
  muted: "#6B7280",
  success: "#15803D",
  amber: "#B45309",
};

const FONT_DISPLAY = "'Fraunces', serif";
const FONT_BODY = "'Inter', sans-serif";
const FONT_MONO = "'JetBrains Mono', monospace";

const DONATION_COOLDOWN_DAYS = 90;

interface DonorProfile {
  name: string;
  email: string;
  bloodGroup: string;
  age: number;
  phone: string;
  city: string;
  address?: string;
  pincode: string;
}

interface Donations {
  id: number;
  units: number;
  status: string;
  transactionDate: string;
}

// ---- Signature element: teardrop blood-group badge ----
function BloodDrop({ group, size = 72 }: { group: string; size?: number }) {
  return (
    <div
      className="relative flex items-center justify-center shrink-0"
      style={{ width: size, height: size * 1.15 }}
    >
      <svg
        viewBox="0 0 100 115"
        width={size}
        height={size * 1.15}
        className="absolute inset-0 drop-shadow-md"
      >
        <path
          d="M50 4 C50 4 12 46 12 74 C12 96.7 29.6 113 50 113 C70.4 113 88 96.7 88 74 C88 46 50 4 50 4 Z"
          fill={COLORS.crimson}
        />
        <path
          d="M50 4 C50 4 12 46 12 74 C12 96.7 29.6 113 50 113 C70.4 113 88 96.7 88 74 C88 46 50 4 50 4 Z"
          fill="white"
          opacity="0.08"
        />
      </svg>
      <span
        className="relative text-white font-semibold z-10"
        style={{
          fontFamily: FONT_MONO,
          fontSize: size * 0.24,
          marginTop: size * 0.12,
        }}
      >
        {group || "—"}
      </span>
    </div>
  );
}

function statusBadgeStyle(status: string) {
  const s = status?.toLowerCase();
  if (s === "completed" || s === "approved")
    return { backgroundColor: "#DCFCE7", color: COLORS.success };
  if (s === "pending")
    return { backgroundColor: "#FEF3C7", color: COLORS.amber };
  if (s === "rejected" || s === "cancelled")
    return { backgroundColor: "#FEE2E2", color: COLORS.crimsonDark };
  return { backgroundColor: "#E5E7EB", color: COLORS.muted };
}

export default function DonorDashboard() {
  const [profile, setProfile] = useState<DonorProfile | null>(null);
  const [donations, setDonations] = useState<Donations[]>([]);
  const [loading, setLoading] = useState(true);

  // Edit modal state — only used as a draft while the dialog is open
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [draft, setDraft] = useState({
    bloodGroup: "",
    age: "",
    phone: "",
    city: "",
    address: "",
    pincode: "",
  });

  const [totalDonations, setTotalDonations] = useState(0);

  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("role");
    localStorage.removeItem("token");
    navigate("/login");
  };

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get(`/donor/profile`);
        const data = response.data;
        setProfile(data);

        const donationResponse = await api.get(`/donor/donationHistory`);
        const donationData = donationResponse.data;
        setDonations(donationData);

        setTotalDonations(donationData.length);
      } catch (error) {
        console.error(error);
        toast.error("Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const openEditModal = () => {
    if (!profile) return;
    setDraft({
      bloodGroup: profile.bloodGroup || "",
      age: String(profile.age || ""),
      phone: profile.phone || "",
      city: profile.city || "",
      address: profile.address || "",
      pincode: profile.pincode || "",
    });
    setIsEditOpen(true);
  };

  const updateProfile = async () => {
    setSaving(true);
    try {
      const response = await api.put(`/donor/profile`, {
        bloodGroup: draft.bloodGroup,
        age: Number(draft.age),
        phone: draft.phone,
        city: draft.city,
        address: draft.address,
        pincode: draft.pincode,
      });

      setProfile((prev) =>
        prev
          ? {
              ...prev,
              bloodGroup: draft.bloodGroup,
              age: Number(draft.age),
              phone: draft.phone,
              city: draft.city,
              address: draft.address,
              pincode: draft.pincode,
            }
          : prev,
      );

      toast.success(response.data.message || "Profile updated");
      setIsEditOpen(false);
    } catch (error: any) {
      toast.error(error?.response?.data?.message || "Unable to update profile");
    } finally {
      setSaving(false);
    }
  };

  // Eligibility: derived from most recent donation date
  const eligibility = useMemo(() => {
    if (donations.length === 0) {
      return {
        ready: true,
        daysLeft: 0,
        progressPct: 100,
        lastDate: null as Date | null,
      };
    }
    const mostRecent = donations.reduce((latest, d) => {
      const date = new Date(d.transactionDate);
      return date > latest ? date : latest;
    }, new Date(0));

    const daysSince = Math.floor(
      (Date.now() - mostRecent.getTime()) / (1000 * 60 * 60 * 24),
    );
    const daysLeft = Math.max(0, DONATION_COOLDOWN_DAYS - daysSince);
    const progressPct = Math.min(
      100,
      Math.round((daysSince / DONATION_COOLDOWN_DAYS) * 100),
    );

    return {
      ready: daysLeft === 0,
      daysLeft,
      progressPct,
      lastDate: mostRecent,
    };
  }, [donations]);

  const livesImpacted = (totalDonations || 0) * 3;

  if (loading) {
    return (
      <div
        className="flex min-h-screen items-center justify-center"
        style={{ backgroundColor: COLORS.paper, fontFamily: FONT_BODY }}
      >
        <p style={{ color: COLORS.muted }}>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div
      className="min-h-screen p-6"
      style={{ backgroundColor: COLORS.paper, fontFamily: FONT_BODY }}
    >
      <div className="mx-auto max-w-6xl space-y-6">
        {/* Header */}
        <Card className="border-none shadow-sm overflow-hidden">
          <CardContent className="flex flex-col gap-5 p-6 md:flex-row md:items-center md:justify-between">
            <div className="flex items-center gap-5">
              <BloodDrop group={profile?.bloodGroup || ""} />
              <div>
                <h1
                  className="text-3xl md:text-4xl tracking-tight"
                  style={{
                    fontFamily: FONT_DISPLAY,
                    color: COLORS.ink,
                    fontWeight: 600,
                  }}
                >
                  Welcome back, {profile?.name}
                </h1>
                <p className="mt-1" style={{ color: COLORS.muted }}>
                  Managing a donor profile that helps keep the blood supply
                  moving.
                </p>
              </div>
            </div>

            <Button
              variant="outline"
              onClick={handleLogout}
              className="self-start md:self-auto"
            >
              <LogOut className="mr-2 h-4 w-4" />
              Logout
            </Button>
          </CardContent>
        </Card>

        {/* Stats */}
        <div className="grid gap-4 md:grid-cols-3">
          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm" style={{ color: COLORS.muted }}>
                  Total Donations
                </p>
                <p
                  className="mt-1 text-3xl font-semibold"
                  style={{ fontFamily: FONT_MONO, color: COLORS.ink }}
                >
                  {totalDonations ?? 0}
                </p>
              </div>
              <Droplet className="h-9 w-9" style={{ color: COLORS.crimson }} />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm" style={{ color: COLORS.muted }}>
                  Lives Impacted
                </p>
                <p
                  className="mt-1 text-3xl font-semibold"
                  style={{ fontFamily: FONT_MONO, color: COLORS.ink }}
                >
                  {livesImpacted}
                </p>
                <p className="text-xs mt-0.5" style={{ color: COLORS.muted }}>
                  ~3 lives per donation
                </p>
              </div>
              <HeartPulse className="h-9 w-9" style={{ color: COLORS.navy }} />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm" style={{ color: COLORS.muted }}>
                    Donor Status
                  </p>
                  <p
                    className="mt-1 text-xl font-semibold"
                    style={{
                      color: eligibility.ready ? COLORS.success : COLORS.amber,
                    }}
                  >
                    {eligibility.ready
                      ? "Ready to donate"
                      : `Cooldown · ${eligibility.daysLeft}d left`}
                  </p>
                </div>
                <Clock3
                  className="h-9 w-9"
                  style={{
                    color: eligibility.ready ? COLORS.success : COLORS.amber,
                  }}
                />
              </div>
              <div
                className="mt-3 h-1.5 w-full rounded-full overflow-hidden"
                style={{ backgroundColor: "#E5E7EB" }}
              >
                <div
                  className="h-full rounded-full transition-all"
                  style={{
                    width: `${eligibility.progressPct}%`,
                    backgroundColor: eligibility.ready
                      ? COLORS.success
                      : COLORS.crimson,
                  }}
                />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Profile — read-only display with an Edit modal */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
              Profile Information
            </CardTitle>
            <Button onClick={openEditModal} size="sm">
              <Pencil className="mr-2 h-3.5 w-3.5" />
              Edit Profile
            </Button>
          </CardHeader>

          <CardContent className="grid gap-6 md:grid-cols-3">
            {[
              { label: "Name", value: profile?.name },
              { label: "Email", value: profile?.email },
              { label: "Blood Group", value: profile?.bloodGroup },
              { label: "Age", value: profile?.age },
              { label: "Phone", value: profile?.phone },
              { label: "City", value: profile?.city },
              { label: "Address", value: profile?.address || "—" },
              { label: "Pincode", value: profile?.pincode },
            ].map((field) => (
              <div key={field.label}>
                <p
                  className="text-xs uppercase tracking-wide"
                  style={{ color: COLORS.muted }}
                >
                  {field.label}
                </p>
                <p
                  className="mt-1 text-base"
                  style={{ color: COLORS.ink, fontFamily: FONT_BODY }}
                >
                  {field.value || "—"}
                </p>
              </div>
            ))}
          </CardContent>
        </Card>

        {/* Donation History */}
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
              Donation History
            </CardTitle>
          </CardHeader>

          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Units</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {donations.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={3}
                      className="text-center"
                      style={{ color: COLORS.muted }}
                    >
                      No donations found yet — your first one will show up here.
                    </TableCell>
                  </TableRow>
                ) : (
                  donations.map((tx) => (
                    <TableRow key={tx.id}>
                      <TableCell
                        style={{ fontFamily: FONT_MONO, fontSize: 13 }}
                      >
                        {new Date(tx.transactionDate).toLocaleDateString()}
                      </TableCell>
                      <TableCell
                        style={{ fontFamily: FONT_MONO, fontSize: 13 }}
                      >
                        {tx.units}
                      </TableCell>
                      <TableCell>
                        <Badge style={statusBadgeStyle(tx.status)}>
                          {tx.status}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>

      {/* Edit Profile Modal */}
      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
              Edit Profile
            </DialogTitle>
            <DialogDescription>
              Update your details so we can match you to the right donation
              requests.
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-4 py-2 md:grid-cols-2">
            <div>
              <Label>Blood Group</Label>
              <Select
                value={draft.bloodGroup}
                onValueChange={(value) =>
                  setDraft((d) => ({ ...d, bloodGroup: value }))
                }
              >
                <SelectTrigger className="mt-2">
                  <SelectValue placeholder="Select Blood Group" />
                </SelectTrigger>
                <SelectContent>
                  {["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"].map(
                    (bg) => (
                      <SelectItem key={bg} value={bg}>
                        {bg}
                      </SelectItem>
                    ),
                  )}
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label>Age</Label>
              <Input
                className="mt-2"
                type="number"
                value={draft.age}
                onChange={(e) =>
                  setDraft((d) => ({ ...d, age: e.target.value }))
                }
              />
            </div>

            <div>
              <Label>Phone</Label>
              <Input
                className="mt-2"
                value={draft.phone}
                onChange={(e) =>
                  setDraft((d) => ({ ...d, phone: e.target.value }))
                }
              />
            </div>

            <div>
              <Label>City</Label>
              <Select
                value={draft.city}
                onValueChange={(value) =>
                  setDraft((d) => ({ ...d, city: value }))
                }
              >
                <SelectTrigger className="mt-2">
                  <SelectValue placeholder="Select City" />
                </SelectTrigger>
                <SelectContent>
                  {[
                    ["DELHI", "Delhi"],
                    ["MUMBAI", "Mumbai"],
                    ["BENGALURU", "Bengaluru"],
                    ["CHENNAI", "Chennai"],
                    ["HYDERABAD", "Hyderabad"],
                    ["KOLKATA", "Kolkata"],
                    ["PUNE", "Pune"],
                    ["JAIPUR", "Jaipur"],
                    ["LUCKNOW", "Lucknow"],
                    ["AHMEDABAD", "Ahmedabad"],
                    ["OTHER", "Other"],
                  ].map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="md:col-span-2">
              <Label>Address</Label>
              <Input
                className="mt-2"
                value={draft.address}
                onChange={(e) =>
                  setDraft((d) => ({ ...d, address: e.target.value }))
                }
                placeholder="Enter your address"
              />
            </div>

            <div>
              <Label>Pincode</Label>
              <Input
                className="mt-2"
                value={draft.pincode}
                onChange={(e) =>
                  setDraft((d) => ({ ...d, pincode: e.target.value }))
                }
                placeholder="Enter pincode"
                maxLength={6}
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsEditOpen(false)}
              disabled={saving}
            >
              Cancel
            </Button>
            <Button onClick={updateProfile} disabled={saving}>
              {saving ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
