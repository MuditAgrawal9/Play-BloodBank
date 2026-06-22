import { useEffect, useState } from "react";

import api from "../services/api";
import AdminLayout from "../components/AdminLayout";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";

import type { User } from "../types/User";

export default function Hospitals() {
  const [hospitals, setHospitals] = useState<User[]>([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHospitals = async () => {
      try {
        const response = await api.get("/admin/hospitals");

        setHospitals(response.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchHospitals();
  }, []);

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Hospitals</CardTitle>
        </CardHeader>

        <CardContent>
          {loading ? (
            <p>Loading hospitals...</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>

                  <TableHead>Name</TableHead>

                  <TableHead>Email</TableHead>

                  <TableHead>Phone</TableHead>

                  <TableHead>City</TableHead>

                  <TableHead>Address</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {hospitals.map((hospital) => (
                  <TableRow key={hospital.id}>
                    <TableCell>{hospital.id}</TableCell>

                    <TableCell>{hospital.name}</TableCell>

                    <TableCell>{hospital.email}</TableCell>

                    <TableCell>{hospital.phone}</TableCell>

                    <TableCell>{hospital.city}</TableCell>

                    <TableCell>{hospital.address}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </>
  );
}
