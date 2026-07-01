import { NavLink, useNavigate } from "react-router-dom";

import { Button } from "./ui/button";

export default function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // localStorage.removeItem("user");
    localStorage.removeItem("role");
    localStorage.removeItem("token");

    navigate("/login");
  };

  return (
    <nav className="border-b bg-white">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
        <h1 className="text-xl font-bold">Blood Bank</h1>

        <div className="flex items-center gap-6">
          <NavLink
            to="/admin"
            className={({ isActive }) =>
              isActive ? "font-semibold text-primary" : "text-muted-foreground"
            }
          >
            Dashboard
          </NavLink>

          <NavLink
            to="/inventory"
            className={({ isActive }) =>
              isActive ? "font-semibold text-primary" : "text-muted-foreground"
            }
          >
            Inventory
          </NavLink>

          <NavLink
            to="/transactions"
            className={({ isActive }) =>
              isActive ? "font-semibold text-primary" : "text-muted-foreground"
            }
          >
            Transactions
          </NavLink>

          <Button variant="outline" onClick={handleLogout}>
            Logout
          </Button>
        </div>
      </div>
    </nav>
  );
}
