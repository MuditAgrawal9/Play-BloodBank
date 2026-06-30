import { useEffect, useState } from "react";
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

import { Droplets, HeartHandshake, LogOut, UserCheck } from "lucide-react";

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

interface DonorProfile {
  name: string;
  email: string;
  bloodGroup: string;
  age: number;
  phone: string;
  city: string;
  totalDonations: number;
}

interface Donations {
  id: number;
  units: number;
  status: string;
  transactionDate: string;
}

export default function DonorDashboard() {
  const [profile, setProfile] = useState<DonorProfile | null>(null);

  const [bloodGroup, setBloodGroup] = useState("");
  const [age, setAge] = useState("");
  const [phone, setPhone] = useState("");
  const [city, setCity] = useState("");

  const [loading, setLoading] = useState(true);

  // const user = JSON.parse(localStorage.getItem("user")!);

  const [donations, setDonations] = useState<Donations[]>([]);

  const navigate = useNavigate();

  const handleLogout = () => {
    // localStorage.removeItem("user");
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

        setBloodGroup(data.bloodGroup || "");
        setAge(String(data.age || ""));
        setPhone(data.phone || "");
        setCity(data.city || "");

        //donation History
        const donationResponse = await api.get(
          `/donor/donationHistory`,
        );

        setDonations(donationResponse.data);
      } catch (error) {
        console.error(error);
        toast.error("Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const updateProfile = async () => {
    try {
      const response = await api.put(`/donor/profile`, {
        bloodGroup,
        age: Number(age),
        phone,
        city,
      });

      setProfile((prev) =>
        prev
          ? {
              ...prev,
              bloodGroup,
              age: Number(age),
              phone,
              city,
            }
          : prev,
      );

      toast.success(response.data.message);
    } catch (error: any) {
      toast.error(error?.response?.data?.message || "Unable to update profile");
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-muted-foreground">Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-muted/30 p-6">
      <div className="mx-auto max-w-7xl space-y-6">
        {/* Header */}
        <Card className="border-none shadow-sm">
          <CardContent className="flex flex-col gap-4 p-6 md:flex-row md:items-center md:justify-between">
            <div>
              <h1 className="text-4xl font-bold tracking-tight">
                Welcome Back, {profile?.name}
              </h1>

              <p className="mt-2 text-muted-foreground">
                Manage your donor profile and track your contribution to saving
                lives.
              </p>
            </div>

            <div>
              <span className="inline-flex items-center rounded-full bg-red-100 px-4 py-2 text-sm font-semibold text-red-700">
                Blood Group: {profile?.bloodGroup}
              </span>
            </div>

            <Button variant="outline" onClick={handleLogout}>
              <LogOut className="mr-2 h-4 w-4" />
              Logout
            </Button>
          </CardContent>
        </Card>

        {/* Statistics */}
        {/* <div className="grid gap-4 md:grid-cols-3">
          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm text-muted-foreground">Total Donations</p>

                <p className="mt-1 text-3xl font-bold">
                  {profile?.totalDonations}
                </p>
              </div>

              <HeartHandshake className="h-10 w-10 text-red-500" />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm text-muted-foreground">Blood Group</p>

                <p className="mt-1 text-3xl font-bold">{profile?.bloodGroup}</p>
              </div>

              <Droplets className="h-10 w-10 text-red-500" />
            </CardContent>
          </Card>

          <Card className="shadow-sm">
            <CardContent className="flex items-center justify-between pt-6">
              <div>
                <p className="text-sm text-muted-foreground">Donor Status</p>

                <p className="mt-1 text-xl font-semibold text-green-600">
                  Active
                </p>
              </div>

              <UserCheck className="h-10 w-10 text-green-600" />
            </CardContent>
          </Card>
        </div> */}

        {/* Profile Form */}
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle>Profile Information</CardTitle>
          </CardHeader>

          <CardContent className="grid gap-6 md:grid-cols-2">
            <div>
              <Label>Name</Label>

              <Input value={profile?.name ?? ""} disabled className="mt-2" />
            </div>

            <div>
              <Label>Email</Label>

              <Input value={profile?.email ?? ""} disabled className="mt-2" />
            </div>

            <div>
              <Label>Blood Group</Label>

              <Select
                value={bloodGroup}
                onValueChange={(value) => setBloodGroup(value)}
              >
                <SelectTrigger className="mt-2">
                  <SelectValue placeholder="Select Blood Group" />
                </SelectTrigger>

                <SelectContent>
                  <SelectItem value="A+">A+</SelectItem>
                  <SelectItem value="A-">A-</SelectItem>
                  <SelectItem value="B+">B+</SelectItem>
                  <SelectItem value="B-">B-</SelectItem>
                  <SelectItem value="AB+">AB+</SelectItem>
                  <SelectItem value="AB-">AB-</SelectItem>
                  <SelectItem value="O+">O+</SelectItem>
                  <SelectItem value="O-">O-</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label>Age</Label>

              <Input
                className="mt-2"
                type="number"
                value={age}
                onChange={(e) => setAge(e.target.value)}
              />
            </div>

            <div>
              <Label>Phone</Label>

              <Input
                className="mt-2"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            </div>

            <div>
              <Label>City</Label>

              <Input
                className="mt-2"
                value={city}
                onChange={(e) => setCity(e.target.value)}
              />
            </div>
          </CardContent>

          <div className="px-6 pb-6">
            <Button
              size="lg"
              onClick={updateProfile}
              className="w-full md:w-auto"
            >
              Save Changes
            </Button>
          </div>
        </Card>

        {/* {Donation History} */}
        <Card>
          <CardHeader>
            <CardTitle>Donation History</CardTitle>
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
                    <TableCell colSpan={3} className="text-center">
                      No donations found
                    </TableCell>
                  </TableRow>
                ) : (
                  donations.map((tx) => (
                    <TableRow key={tx.id}>
                      <TableCell>
                        {new Date(tx.transactionDate).toLocaleDateString()}
                      </TableCell>

                      <TableCell>{tx.units}</TableCell>

                      <TableCell>
                        <Badge>{tx.status}</Badge>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
