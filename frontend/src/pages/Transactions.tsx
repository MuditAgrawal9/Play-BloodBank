import { useEffect, useState } from "react";

import api from "../services/api";

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

import type { Transaction } from "../types/Transaction";
import { Button } from "../components/ui/button";
import { toast } from "sonner";

export default function Transactions() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  const [loading, setLoading] = useState(true);

  const fetchTransactions = async () => {
    try {
      const response = await api.get("/admin/transactions");

      setTransactions(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const approveTransaction = async (id: number) => {
    try {
      const response = await api.post(`/transaction/${id}/approve`);

      toast.success(response.data.message);

      fetchTransactions();
    } catch (error : any) {
      toast.error(error?.response?.data?.message || "Something went wrong");
      console.error(error);
    }
  };

  const rejectTransaction = async (id: number) => {
    try {
      const response = await api.post(`/transaction/${id}/reject`);
      toast.success(response.data.message);
      fetchTransactions();
      
    } catch (error : any) {
      toast.error(error?.response?.data?.message || "Something went wrong");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, []);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "COMPLETED":
        return (
          <Badge className="bg-green-500 hover:bg-green-500">Completed</Badge>
        );

      case "APPROVED":
        return (
          <Badge className="bg-blue-500 hover:bg-blue-500">Approved</Badge>
        );

      case "PENDING":
        return (
          <Badge className="bg-yellow-500 hover:bg-yellow-500">Pending</Badge>
        );

      case "REJECTED":
        return <Badge variant="destructive">Rejected</Badge>;

      default:
        return <Badge>{status}</Badge>;
    }
  };

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Blood Transactions</CardTitle>
        </CardHeader>

        <CardContent>
          {loading ? (
            <p>Loading transactions...</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>

                  <TableHead>Blood Group</TableHead>

                  <TableHead>Type</TableHead>

                  <TableHead>Units</TableHead>

                  <TableHead>Status</TableHead>

                  <TableHead>Date</TableHead>

                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {transactions.map((tx) => (
                  <TableRow key={tx.id}>
                    <TableCell>{tx.user.name}</TableCell>

                    <TableCell className="font-medium">
                      {tx.bloodGroup}
                    </TableCell>

                    <TableCell>{tx.transactionType}</TableCell>

                    <TableCell>{tx.units}</TableCell>

                    <TableCell>{getStatusBadge(tx.status)}</TableCell>

                    <TableCell>
                      {new Date(tx.transactionDate).toLocaleString()}
                    </TableCell>

                    <TableCell>
                      {tx.status === "PENDING" && (
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            onClick={() => approveTransaction(tx.id)}
                          >
                            Approve
                          </Button>

                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => rejectTransaction(tx.id)}
                          >
                            Reject
                          </Button>
                        </div>
                      )}
                    </TableCell>
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
