import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMeApi } from "../api/auth";
import BottomNav from "../components/BottomNav";

interface UserInfo {
  id: number;
  phone: string;
  nickname: string;
  avatar?: string;
  createdAt?: string;
  updateAt?: string;
}

export default function ProfilePage() {
  const navigate = useNavigate();

  const [user, setUser] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);

  const isLoggedIn = user !== null;

  useEffect(() => {
    const fetchMe = async () => {
      const token = localStorage.getItem("token");

      // No token means guest mode
      if (!token) {
        setUser(null);
        setLoading(false);
        return;
      }

      try {
        const res = await getMeApi();

        if (!res.data.success) {
          // Token exists but is invalid or expired
          localStorage.removeItem("token");
          localStorage.removeItem("userId");
          localStorage.removeItem("nickname");

          setUser(null);
          return;
        }

        setUser(res.data.data);
      } catch (error) {
        console.error(error);

        // API failed or token invalid: stay on profile page as guest
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("nickname");

        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchMe();
  }, []);

  const handleAuthButton = () => {
    if (isLoggedIn) {
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      localStorage.removeItem("nickname");

      setUser(null);
      return;
    }

    navigate("/login");
  };

  if (loading) {
    return (
      <div className="screen">
        <div className="profile-loading">Loading...</div>
      </div>
    );
  }

  const displayName = isLoggedIn ? user.nickname : "Guest";
  const displayId = isLoggedIn ? user.id : "-";
  const displayPhone = isLoggedIn ? user.phone : "Not logged in";
  const avatarText = isLoggedIn
    ? user.nickname.charAt(0).toLowerCase()
    : "g";

  return (
    <div className="screen">
      <div className="profile-page">
        <div className="profile-hero">
          <div className="profile-top-icons">
            <button className="icon-btn soft">☰</button>
            <div className="profile-top-right">
              <button className="icon-btn soft">⌗</button>
              <button className="icon-btn soft">↗</button>
            </div>
          </div>

          <div className="profile-head">
            <div className="avatar-wrap">
              <div className="avatar">{avatarText}</div>
              <div className="avatar-plus">+</div>
            </div>

            <div className="profile-main-info">
              <h2 className="profile-name">{displayName}</h2>
              <p className="profile-id">mlog ID: {displayId}</p>
              <p className="profile-id">Phone: {displayPhone}</p>
            </div>
          </div>

          <p className="bio-text">
            {isLoggedIn
              ? "Tap here to fill in your bio"
              : "Log in to create your food profile"}
          </p>

          <div className="stats-row">
            <div className="stat-item">
              <strong>0</strong>
              <span>Following</span>
            </div>
            <div className="stat-item">
              <strong>0</strong>
              <span>Followers</span>
            </div>
            <div className="stat-item">
              <strong>0</strong>
              <span>Likes & Saves</span>
            </div>
          </div>

          <div className="action-row">
            <button className="outline-small-btn">
              {isLoggedIn ? "Edit profile" : "Guest mode"}
            </button>

            <button className="outline-small-btn" onClick={handleAuthButton}>
              {isLoggedIn ? "Logout" : "Login"}
            </button>
          </div>

          <div className="feature-cards">
            <div className="feature-card">
              <div className="card-title">Sparks</div>
              <div className="card-sub">Find sparks</div>
            </div>
            <div className="feature-card">
              <div className="card-title">History</div>
              <div className="card-sub">Viewed notes</div>
            </div>
            <div className="feature-card">
              <div className="card-title">Mlog Creator</div>
              <div className="card-sub">Empowering creators</div>
            </div>
          </div>
        </div>

        <div className="tab-bar">
          <button className="tab active">Notes</button>
          <button className="tab">Saves</button>
          <button className="tab">Likes</button>
          <button className="search-btn">⌕</button>
        </div>

        <div className="empty-post-area">
          <div className="empty-icon">🖼</div>
          <p className="empty-title">
            {isLoggedIn
              ? "Clear food photos in your album"
              : "Log in and share your food moments"}
          </p>
          <button
            className="outline-post-btn"
            onClick={() => {
              if (!isLoggedIn) {
                navigate("/login");
              }
            }}
          >
            {isLoggedIn ? "Post" : "Login"}
          </button>
        </div>
      </div>

      <BottomNav />
    </div>
  );
}