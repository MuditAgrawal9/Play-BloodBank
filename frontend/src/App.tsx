import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Signup from "./pages/Signup";
import ProtectedRoute from "./components/ProtectedRoute";
import AdminDashboard from "./pages/AdminDashboard";
import AdminLayout from "./components/AdminLayout";
import Inventory from "./pages/Inventory";
import Transactions from "./pages/Transactions";
import Users from "./pages/Users";
import Donors from "./pages/Donors";
import Hospitals from "./pages/Hospitals";
import DonorDashboard from "./pages/DonorDashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/signup" element={<Signup />} />

        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <AdminDashboard />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/inventory"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <Inventory />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/transactions"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <Transactions />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/users"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <Users />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/donors"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <Donors />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/hospitals"
          element={
            <ProtectedRoute>
              <AdminLayout>
                <Hospitals />
              </AdminLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/donor"
          element={
            <ProtectedRoute>
                <DonorDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
