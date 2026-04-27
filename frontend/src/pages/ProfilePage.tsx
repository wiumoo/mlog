import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMeApi } from "../api/auth";

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

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await getMeApi();

        if (!res.data.success) {
          alert(res.data.errorMsg || "로그인이 필요합니다.");
          localStorage.removeItem("token");
          localStorage.removeItem("userId");
          localStorage.removeItem("nickname");
          navigate("/");
          return;
        }

        setUser(res.data.data);
      } catch (error) {
        console.error(error);
        alert("로그인 정보를 불러오지 못했습니다.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("nickname");
        navigate("/");
      } finally {
        setLoading(false);
      }
    };

    fetchMe();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("nickname");
    navigate("/");
  };

  if (loading) {
    return (
      <div className="screen">
        <div className="profile-loading">Loading...</div>
      </div>
    );
  }

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
              <div className="avatar">
                {user?.nickname ? user.nickname.charAt(0).toLowerCase() : "m"}
              </div>
              <div className="avatar-plus">+</div>
            </div>

            <div className="profile-main-info">
              <h2 className="profile-name">{user?.nickname || "mlog user"}</h2>
              <p className="profile-id">mlog ID: {user?.id}</p>
              <p className="profile-id">Phone: {user?.phone}</p>
            </div>
          </div>

          <p className="bio-text">Tap here to fill in your bio</p>

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
            <button className="outline-small-btn">Edit profile</button>
            <button className="outline-small-btn" onClick={handleLogout}>
              Logout
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
          <p className="empty-title">Clear food photos in your album</p>
          <button className="outline-post-btn">Post</button>
        </div>

        <div className="bottom-nav">
          <div className="nav-item active">
            <span>⌂</span>
            <span>Home</span>
          </div>
          <div className="nav-item">
            <span>▣</span>
            <span>Market</span>
          </div>
          <div className="nav-center">+</div>
          <div className="nav-item">
            <span>◌</span>
            <span>Messages</span>
          </div>
          <div className="nav-item">
            <span>●</span>
            <span>Me</span>
          </div>
        </div>
      </div>
    </div>
  );
}