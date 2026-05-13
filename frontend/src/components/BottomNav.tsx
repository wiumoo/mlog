import { useLocation, useNavigate } from "react-router-dom";
import "../styles.css";

export default function BottomNav() {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  return (
    <nav className="bottom-nav">
      <button
        className={isActive("/home") ? "nav-item active" : "nav-item"}
        onClick={() => navigate("/home")}
      >
        <span>⌂</span>
        <span>Home</span>
      </button>

      <button
        className={isActive("/shops") ? "nav-item active" : "nav-item"}
        onClick={() => navigate("/shops")}
      >
        <span>▣</span>
        <span>List</span>
      </button>

      <button className="nav-center" type="button">
        +
      </button>

      <button className="nav-item" type="button">
        <span>◌</span>
        <span>Messages</span>
      </button>

      <button
        className={isActive("/me") ? "nav-item active" : "nav-item"}
        onClick={() => navigate("/me")}
        >
        <span>●</span>
        <span>Me</span>
      </button>
    </nav>
  );
}