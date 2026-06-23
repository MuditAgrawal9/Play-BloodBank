import { Navigate } from "react-router-dom";

type Props = {
  children: React.ReactNode;
};

export default function PrivateRoute({ children }: Props) {
  const userString = localStorage.getItem("user");

  if (!userString) {
    return <Navigate to="/login" />;
  }

  const user = JSON.parse(userString);

  if (user.role !== "ADMIN") {
    return <Navigate to="/login" />;
  }

  return <>{children}</>;
}