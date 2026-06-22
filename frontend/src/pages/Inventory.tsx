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

import type { Inventory as InventoryType } from "../types/Inventory";

export default function Inventory() {
  const [inventory, setInventory] = useState<InventoryType[]>([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInventory = async () => {
      try {
        const response = await api.get("/admin/inventory");

        setInventory(response.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchInventory();
  }, []);

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Blood Inventory</CardTitle>
        </CardHeader>

        <CardContent>
          {loading ? (
            <p>Loading inventory...</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Blood Group</TableHead>

                  <TableHead>Units Available</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {inventory.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell className="font-medium">
                      {item.bloodGroup}
                    </TableCell>

                    <TableCell>{item.unitsAvailable}</TableCell>
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
