import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../services/api";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import RecordDonationDialog from "./RecordDonationDialog";

export default function AdminDashboard() {
  const navigate = useNavigate();

  const [stats, setStats] = useState({
    totalUsers: 0,
    totalDonors: 0,
    totalHospitals: 0,
    pendingRequests: 0,
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await api.get("/admin/stats");

        setStats(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchStats();
  }, []);

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Admin Dashboard</h1>

          <p className="text-muted-foreground">
            Manage users, donations and requests
          </p>
        </div>

        {/* <Button
          onClick={() =>
            navigate("/admin/donation")
          }
        >
          + Record Donation
        </Button> */}
        <RecordDonationDialog />
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card
          className="cursor-pointer transition hover:shadow-md"
          onClick={() => navigate("/admin/users")}
        >
          <CardHeader>
            <CardTitle>Total Users</CardTitle>
          </CardHeader>

          <CardContent>
            <p className="text-3xl font-bold">{stats.totalUsers}</p>
          </CardContent>
        </Card>

        <Card
          className="cursor-pointer transition hover:shadow-md"
          onClick={() => navigate("/admin/donors")}
        >
          <CardHeader>
            <CardTitle>Donors</CardTitle>
          </CardHeader>

          <CardContent>
            <p className="text-3xl font-bold">{stats.totalDonors}</p>
          </CardContent>
        </Card>

        <Card
          className="cursor-pointer transition hover:shadow-md"
          onClick={() => navigate("/admin/hospitals")}
        >
          <CardHeader>
            <CardTitle>Hospitals</CardTitle>
          </CardHeader>

          <CardContent>
            <p className="text-3xl font-bold">{stats.totalHospitals}</p>
          </CardContent>
        </Card>

        <Card
          className="cursor-pointer transition hover:shadow-md"
          onClick={() => navigate("/transactions")}
        >
          <CardHeader>
            <CardTitle>Pending Requests</CardTitle>
          </CardHeader>

          <CardContent>
            <p className="text-3xl font-bold text-orange-600">
              {stats.pendingRequests}
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
