import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import WelcomePage from "./pages/WelcomePage";
import ProfilePage from "./pages/ProfilePage";
import HomePage from "./pages/HomePage";
import ShopListPage from "./pages/ShopListPage";
import "./styles.css";
import ShopDetailPage from "./pages/ShopDetailPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/shops" element={<ShopListPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/welcome" element={<WelcomePage />} />
      <Route path="/me" element={<ProfilePage />} />  
      <Route path="*" element={<Navigate to="/" replace />} />
    <Route path="/shops/:id" element={<ShopDetailPage />} />
    </Routes>
  );
}

export default App;