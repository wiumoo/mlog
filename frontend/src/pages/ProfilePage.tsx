export default function ProfilePage() {
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
              <div className="avatar">m</div>
              <div className="avatar-plus">+</div>
            </div>

            <div className="profile-main-info">
              <h2 className="profile-name">mlog_USER_01</h2>
              <p className="profile-id">mlog ID: 5161635241</p>
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
            <button className="outline-small-btn">⚙</button>
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