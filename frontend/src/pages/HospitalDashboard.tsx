import { useEffect, useState } from "react";
import axios from "axios";

import api from "../services/api";

import { toast } from "sonner";

import { LogOut } from "lucide-react";

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
import type { User } from "../types/User";

interface HospitalProfile {
  id: number;
  name: string;
  email: string;
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

export default function HospitalDashboard() {
  const navigate = useNavigate();

  const [profile, setProfile] = useState<HospitalProfile | null>(null);

  const [name, setName] = useState("");

  const [bloodGroup, setBloodGroup] = useState("A+");

  const [unitsRequired, setUnitsRequired] = useState("");

  const [requests, setRequests] = useState<BloodRequest[]>([]);

  const [loading, setLoading] = useState(true);

  // const user = JSON.parse(localStorage.getItem("user")!);

  const fetchData = async () => {
    try {
      const profileResponse = await api.get(`/hospital/profile`);

      setProfile(profileResponse.data);

      setName(profileResponse.data.name);

      const requestResponse = await api.get(`/hospital/requests`);

      setRequests(requestResponse.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const updateProfile = async () => {
    try {
      const response = await api.put(`/hospital/profile`, {
        name,
      });

      toast.success(response.data.message);
    } catch (error) {
      toast.error("Unable to update profile");
    }
  };

  const createRequest = async () => {
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
    }
  };

  const logout = () => {
    // localStorage.removeItem("user");
    localStorage.removeItem("role");
    localStorage.removeItem("token");

    navigate("/login");
  };

  const getBadge = (status: string) => {
    switch (status) {
      case "APPROVED":
        return <Badge className="bg-green-500">APPROVED</Badge>;

      case "PENDING":
        return <Badge className="bg-yellow-500">PENDING</Badge>;

      case "REJECTED":
        return <Badge variant="destructive">REJECTED</Badge>;

      default:
        return <Badge>{status}</Badge>;
    }
  };

  if (loading) {
    return <div className="p-6">Loading...</div>;
  }

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Hospital Dashboard</h1>

          <p className="text-muted-foreground">Welcome back, {profile?.name}</p>
        </div>

        <Button variant="outline" onClick={logout}>
          <LogOut className="mr-2 h-4 w-4" />
          Logout
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Profile</CardTitle>
          </CardHeader>

          <CardContent className="space-y-4">
            <div>
              <Label>Hospital Name</Label>

              <Input value={name} onChange={(e) => setName(e.target.value)} />
            </div>

            <div>
              <Label>Email</Label>

              <Input value={profile?.email} disabled />
            </div>

            <Button onClick={updateProfile}>Save Changes</Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Request Blood</CardTitle>
          </CardHeader>

          <CardContent className="space-y-4">
            <div>
              <Label>Blood Group</Label>

              {/* <Input
                value={bloodGroup}
                onChange={(e) => setBloodGroup(e.target.value)}
              /> */}
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
              <Label>Units Required</Label>

              <Input
                type="number"
                value={unitsRequired}
                onChange={(e) => setUnitsRequired(e.target.value)}
              />
            </div>

            <Button onClick={createRequest}>Submit Request</Button>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Request History</CardTitle>
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
              {requests.map((request) => (
                <TableRow key={request.id}>
                  <TableCell>{request.bloodGroup}</TableCell>

                  <TableCell>{request.units}</TableCell>

                  <TableCell>{getBadge(request.status)}</TableCell>

                  <TableCell>
                    {new Date(request.transactionDate).toLocaleDateString()}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
