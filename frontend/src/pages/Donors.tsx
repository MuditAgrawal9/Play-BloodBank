import { useEffect, useState } from "react";

import api from "../services/api";

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

import type { Donor } from "../types/Donor";

export default function Donors() {
  const [donors, setDonors] = useState<Donor[]>([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDonors = async () => {
      try {
        const response = await api.get("/admin/donors");

        setDonors(response.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchDonors();
  }, []);

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Donors</CardTitle>
        </CardHeader>

        <CardContent>
          {loading ? (
            <p>Loading donors...</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>

                  <TableHead>Email</TableHead>

                  <TableHead>Blood Group</TableHead>

                  <TableHead>Age</TableHead>

                  <TableHead>Phone</TableHead>

                  <TableHead>City</TableHead>

                  <TableHead>Address</TableHead>

                  <TableHead>Pin Code</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {donors.map((donor) => (
                  <TableRow key={donor.id}>
                    <TableCell>{donor.user.name}</TableCell>

                    <TableCell>{donor.user.email}</TableCell>

                    <TableCell>{donor.bloodGroup}</TableCell>

                    <TableCell>{donor.age}</TableCell>

                    <TableCell>{donor.user.phone}</TableCell>

                    <TableCell>{donor.user.city}</TableCell>

                    <TableCell>{donor.user.address}</TableCell>

                    <TableCell>{donor.user.pincode}</TableCell>
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
