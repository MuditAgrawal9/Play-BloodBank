import { Navigate } from "react-router-dom";

type Props = {
  children: React.ReactNode;
};

export default function PrivateRoute({ children }: Props) {
  // const userString = localStorage.getItem("user");

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!role || !token) {
    return <Navigate to="/login" />;
  }

  // const user = JSON.parse(userString);

  if (role !== "ADMIN") {
    return <Navigate to="/login" />;
  }

  return <>{children}</>;
}