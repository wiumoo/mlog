import { useNavigate } from "react-router-dom";
import { useState } from "react";

export default function WelcomePage() {
  const navigate = useNavigate();
  const [agreed, setAgreed] = useState(true);

  return (
    <div className="screen">
      <div className="top-row right-only">
        <button className="text-btn">Help</button>
      </div>

      <div className="content welcome-content">
        <div className="brand-block">
          <h1 className="brand-logo">mlog</h1>
          <p className="brand-subtitle">메뉴 걱정 없을때까지</p>
        </div>

        <div className="phone-preview-row">
          <span className="preview-phone">+86 010 **** 5678</span>
          <button className="small-pill-btn">Change</button>
        </div>

        <button className="primary-btn" onClick={() => navigate("/me")}>
          Log in with current phone number
        </button>

        <button className="outline-btn">WeChat</button>
        <button className="outline-btn">Apple</button>

        <div className="agree-row welcome-agree">
          <button
            className={`check-circle ${agreed ? "checked" : ""}`}
            onClick={() => setAgreed(!agreed)}
          >
            {agreed ? "✓" : ""}
          </button>
          <p className="agree-text">
            I have read and agree to the Terms, Privacy Policy, Rules Regarding
            the Protection of Personal Information of Minors
          </p>
        </div>

        <button className="other-login-link" onClick={() => navigate("/")}>
          Other login methods ›
        </button>
      </div>
    </div>
  );
}