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

import type { User } from "../types/User";

export default function Users() {
  const [users, setUsers] = useState<User[]>([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await api.get("/admin/users");

        setUsers(response.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Users</CardTitle>
        </CardHeader>

        <CardContent>
          {loading ? (
            <p>Loading users...</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>

                  <TableHead>Email</TableHead>

                  <TableHead>Role</TableHead>

                  <TableHead>Phone</TableHead>

                  <TableHead>City</TableHead>

                  <TableHead>Address</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.name}</TableCell>

                    <TableCell>{user.email}</TableCell>

                    <TableCell>{user.role}</TableCell>

                    <TableCell>{user.phone}</TableCell>

                    <TableCell>{user.city}</TableCell>

                    <TableCell>{user.address}</TableCell>
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
