import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import React from "react";

import api from "../services/api";

import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import type { Donor } from "../types/Donor";

export default function RecordDonationDialog() {
  const [open, setOpen] = useState(false);

  const [donors, setDonors] = useState<Donor[]>([]);

  const [donorId, setDonorId] = useState("");

  const [units, setUnits] = useState("");

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchDonors = async () => {
      try {
        const response = await api.get("/admin/donors");

        setDonors(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    if (open) {
      fetchDonors();
    }
  }, [open]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!donorId || !units) {
      toast.error("Please fill all fields");
      return;
    }

    try {
      setLoading(true);

      const response = await api.post("/admin/donation", {
        donorId: Number(donorId),
        units: Number(units),
      });

      toast.success(response.data.message);

      setDonorId("");
      setUnits("");

      setOpen(false);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        toast.error(error.response?.data?.message || "Something went wrong");
      } else {
        toast.error("Something went wrong");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>+ Record Donation</Button>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record Donation</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Donor</Label>

            <Select value={donorId} onValueChange={setDonorId}>
              <SelectTrigger>
                <SelectValue placeholder="Select donor" />
              </SelectTrigger>

              <SelectContent>
                {donors.map((donor) => (
                  <SelectItem key={donor.id} value={String(donor.id)}>
                    {donor.user.name} ({donor.bloodGroup})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>Units</Label>

            <Input
              type="number"
              min="1"
              value={units}
              onChange={(e) => setUnits(e.target.value)}
              placeholder="Enter units"
            />
          </div>

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Recording..." : "Record Donation"}
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
