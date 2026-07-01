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
import PrivateRoute from "./components/PrivateRoute";
import HospitalDashboard from "./pages/HospitalDashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/signup" element={<Signup />} />

        <Route
          path="/admin"
          element={
            <PrivateRoute>
              <AdminLayout>
                <AdminDashboard />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/inventory"
          element={
            <PrivateRoute>
              <AdminLayout>
                <Inventory />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/transactions"
          element={
            <PrivateRoute>
              <AdminLayout>
                <Transactions />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/users"
          element={
            <PrivateRoute>
              <AdminLayout>
                <Users />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/donors"
          element={
            <PrivateRoute>
              <AdminLayout>
                <Donors />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/hospitals"
          element={
            <PrivateRoute>
              <AdminLayout>
                <Hospitals />
              </AdminLayout>
            </PrivateRoute>
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

        <Route
          path="/hospital"
          element={
            <ProtectedRoute>
              <HospitalDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
