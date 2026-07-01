import { useEffect, useMemo, useState } from "react";
import axios from "axios";

import api from "../services/api";

import { toast } from "sonner";

import { LogOut, Pencil, Building2, ClipboardList, Droplet } from "lucide-react";

import { useNavigate } from "react-router-dom";

import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "../components/ui/dialog";
import type { User } from "../types/User";

// ---- Shared design tokens (matches the donor dashboard) ----
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

interface HospitalProfile {
  id: number;
  name: string;
  email: string;
  phone?: string;
  city?: string;
  address?: string;
  pincode?: string;
}

interface BloodRequest {
  id: number;
  bloodGroup: string;
  units: number;
  status: string;
  transactionDate: string;
  transactionType: string;
  user: User;
}

function statusBadgeStyle(status: string) {
  const s = status?.toLowerCase();
  if (s === "approved")
    return { backgroundColor: "#DCFCE7", color: COLORS.success };
  if (s === "pending")
    return { backgroundColor: "#FEF3C7", color: COLORS.amber };
  if (s === "rejected")
    return { backgroundColor: "#FEE2E2", color: COLORS.crimsonDark };
  return { backgroundColor: "#E5E7EB", color: COLORS.muted };
}

const CITIES: [string, string][] = [
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
];

export default function HospitalDashboard() {
  const navigate = useNavigate();

  const [profile, setProfile] = useState<HospitalProfile | null>(null);
  const [requests, setRequests] = useState<BloodRequest[]>([]);
  const [loading, setLoading] = useState(true);

  const [bloodGroup, setBloodGroup] = useState("A+");
  const [unitsRequired, setUnitsRequired] = useState("");
  const [submittingRequest, setSubmittingRequest] = useState(false);

  // Edit modal state
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [draft, setDraft] = useState({
    name: "",
    phone: "",
    city: "",
    address: "",
    pincode: "",
  });

  const fetchData = async () => {
    try {
      const profileResponse = await api.get(`/hospital/profile`);
      setProfile(profileResponse.data);

      const requestResponse = await api.get(`/hospital/requests`);
      setRequests(requestResponse.data);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load dashboard");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const openEditModal = () => {
    if (!profile) return;
    setDraft({
      name: profile.name || "",
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
      const response = await api.put(`/hospital/profile`, {
        name: draft.name,
        phone: draft.phone,
        city: draft.city,
        address: draft.address,
        pincode: draft.pincode,
      });

      setProfile((prev) =>
        prev
          ? {
              ...prev,
              name: draft.name,
              phone: draft.phone,
              city: draft.city,
              address: draft.address,
              pincode: draft.pincode,
            }
          : prev,
      );

      toast.success(response.data.message || "Profile updated");
      setIsEditOpen(false);
    } catch (error) {
      toast.error("Unable to update profile");
    } finally {
      setSaving(false);
    }
  };

  const createRequest = async () => {
    if (!unitsRequired || Number(unitsRequired) <= 0) {
      toast.error("Enter a valid number of units");
      return;
    }
    setSubmittingRequest(true);
    try {
      const response = await api.post(`/hospital/request`, {
        bloodGroup,
        unitsRequired: Number(unitsRequired),
      });

      toast.success(response.data.message);
      setUnitsRequired("");
      fetchData();
    } catch (error) {
      if (axios.isAxiosError(error)) {
        toast.error(
          error.response?.data?.message || "Unable to submit request",
        );
      } else {
        toast.error("Unable to submit request");
      }
    } finally {
      setSubmittingRequest(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("role");
    localStorage.removeItem("token");
    navigate("/login");
  };

  const stats = useMemo(() => {
    const totalUnits = requests.reduce((sum, r) => sum + (r.units || 0), 0);
    const pending = requests.filter((r) => r.status?.toUpperCase() === "PENDING").length;
    const approved = requests.filter((r) => r.status?.toUpperCase() === "APPROVED").length;
    const approvalRate = requests.length ? Math.round((approved / requests.length) * 100) : 0;
    return { totalRequests: requests.length, totalUnits, pending, approvalRate };
  }, [requests]);

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
        <Card className="border-none shadow-sm">
          <CardContent className="flex flex-col gap-5 p-6 md:flex-row md:items-center md:justify-between">
            <div className="flex items-center gap-5">
              <div
                className="flex h-16 w-16 shrink-0 items-center justify-center rounded-xl"
                style={{ backgroundColor: COLORS.navy }}
              >
                <Building2 className="h-8 w-8 text-white" />
              </div>
              <div>
                <h1
                  className="text-3xl md:text-4xl tracking-tight"
                  style={{ fontFamily: FONT_DISPLAY, color: COLORS.ink, fontWeight: 600 }}
                >
                  {profile?.name}
                </h1>
                <p className="mt-1" style={{ color: COLORS.muted }}>
                  Track requests and keep facility details current for donor matching.
                </p>
              </div>
            </div>

            <Button variant="outline" onClick={logout} className="self-start md:self-auto">
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
                  Total Requests
                </p>
                <p
                  className="mt-1 text-3xl font-semibold"
                  style={{ fontFamily: FONT_MONO, color: COLORS.ink }}
                >
                  {stats.totalRequests}
                </p>
              </div>
              <ClipboardList className="h-9 w-9" style={{ color: COLORS.navy }} />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm" style={{ color: COLORS.muted }}>
                  Units Requested
                </p>
                <p
                  className="mt-1 text-3xl font-semibold"
                  style={{ fontFamily: FONT_MONO, color: COLORS.ink }}
                >
                  {stats.totalUnits}
                </p>
              </div>
              <Droplet className="h-9 w-9" style={{ color: COLORS.crimson }} />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm" style={{ color: COLORS.muted }}>
                    Approval Rate
                  </p>
                  <p
                    className="mt-1 text-xl font-semibold"
                    style={{ color: stats.approvalRate >= 50 ? COLORS.success : COLORS.amber }}
                  >
                    {stats.approvalRate}%
                  </p>
                </div>
              </div>
              <div className="mt-3 h-1.5 w-full rounded-full overflow-hidden" style={{ backgroundColor: "#E5E7EB" }}>
                <div
                  className="h-full rounded-full transition-all"
                  style={{
                    width: `${stats.approvalRate}%`,
                    backgroundColor: stats.approvalRate >= 50 ? COLORS.success : COLORS.amber,
                  }}
                />
              </div>
              <p className="mt-2 text-xs" style={{ color: COLORS.muted }}>
                {stats.pending} pending
              </p>
            </CardContent>
          </Card>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {/* Profile — read-only with Edit modal */}
          <Card className="shadow-sm">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
                Facility Profile
              </CardTitle>
              <Button onClick={openEditModal} size="sm">
                <Pencil className="mr-2 h-3.5 w-3.5" />
                Edit Profile
              </Button>
            </CardHeader>

            <CardContent className="grid gap-4 sm:grid-cols-2">
              {[
                { label: "Hospital Name", value: profile?.name },
                { label: "Email", value: profile?.email },
                { label: "Phone", value: profile?.phone || "—" },
                { label: "City", value: profile?.city || "—" },
                { label: "Address", value: profile?.address || "—", span: true },
                { label: "Pincode", value: profile?.pincode || "—" },
              ].map((field) => (
                <div key={field.label} className={field.span ? "sm:col-span-2" : ""}>
                  <p className="text-xs uppercase tracking-wide" style={{ color: COLORS.muted }}>
                    {field.label}
                  </p>
                  <p className="mt-1 text-base" style={{ color: COLORS.ink }}>
                    {field.value}
                  </p>
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Request Blood */}
          <Card className="shadow-sm">
            <CardHeader>
              <CardTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
                Request Blood
              </CardTitle>
            </CardHeader>

            <CardContent className="space-y-4">
              <div>
                <Label>Blood Group</Label>
                <Select value={bloodGroup} onValueChange={(value) => setBloodGroup(value)}>
                  <SelectTrigger className="mt-2">
                    <SelectValue placeholder="Select Blood Group" />
                  </SelectTrigger>
                  <SelectContent>
                    {["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"].map((bg) => (
                      <SelectItem key={bg} value={bg}>
                        {bg}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label>Units Required</Label>
                <Input
                  className="mt-2"
                  type="number"
                  min={1}
                  value={unitsRequired}
                  onChange={(e) => setUnitsRequired(e.target.value)}
                  placeholder="e.g. 2"
                />
              </div>

              <Button onClick={createRequest} disabled={submittingRequest} className="w-full">
                {submittingRequest ? "Submitting..." : "Submit Request"}
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* Request History */}
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle style={{ fontFamily: FONT_DISPLAY, fontWeight: 600 }}>
              Request History
            </CardTitle>
          </CardHeader>

          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Blood Group</TableHead>
                  <TableHead>Units</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Date</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {requests.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center" style={{ color: COLORS.muted }}>
                      No requests yet — submit one above to get started.
                    </TableCell>
                  </TableRow>
                ) : (
                  requests.map((request) => (
                    <TableRow key={request.id}>
                      <TableCell style={{ fontFamily: FONT_MONO, fontSize: 13 }}>
                        {request.bloodGroup}
                      </TableCell>
                      <TableCell style={{ fontFamily: FONT_MONO, fontSize: 13 }}>
                        {request.units}
                      </TableCell>
                      <TableCell>
                        <Badge style={statusBadgeStyle(request.status)}>
                          {request.status}
                        </Badge>
                      </TableCell>
                      <TableCell style={{ fontFamily: FONT_MONO, fontSize: 13 }}>
                        {new Date(request.transactionDate).toLocaleDateString()}
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
              Edit Facility Profile
            </DialogTitle>
            <DialogDescription>
              Keep these details accurate so donors and admins can reach you.
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-4 py-2 sm:grid-cols-2">
            <div className="sm:col-span-2">
              <Label>Hospital Name</Label>
              <Input
                className="mt-2"
                value={draft.name}
                onChange={(e) => setDraft((d) => ({ ...d, name: e.target.value }))}
              />
            </div>

            <div>
              <Label>Phone</Label>
              <Input
                className="mt-2"
                value={draft.phone}
                onChange={(e) => setDraft((d) => ({ ...d, phone: e.target.value }))}
              />
            </div>

            <div>
              <Label>City</Label>
              <Select
                value={draft.city}
                onValueChange={(value) => setDraft((d) => ({ ...d, city: value }))}
              >
                <SelectTrigger className="mt-2">
                  <SelectValue placeholder="Select City" />
                </SelectTrigger>
                <SelectContent>
                  {CITIES.map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="sm:col-span-2">
              <Label>Address</Label>
              <Input
                className="mt-2"
                value={draft.address}
                onChange={(e) => setDraft((d) => ({ ...d, address: e.target.value }))}
                placeholder="Enter facility address"
              />
            </div>

            <div>
              <Label>Pincode</Label>
              <Input
                className="mt-2"
                value={draft.pincode}
                onChange={(e) => setDraft((d) => ({ ...d, pincode: e.target.value }))}
                placeholder="Enter pincode"
                maxLength={6}
              />
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditOpen(false)} disabled={saving}>
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