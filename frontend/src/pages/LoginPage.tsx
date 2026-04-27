import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const navigate = useNavigate();
  const [phone, setPhone] = useState("");
  const [agreed, setAgreed] = useState(true);

  const canLogin = phone.trim() !== "" && agreed;

  const handleLogin = () => {
    if (!canLogin) return;

    // 나중에 여기서 실제 로그인 API 연결
    navigate("/me");
  };

  return (
    <div className="screen">


      <div className="top-row">
        <button className="icon-btn" onClick={() => navigate("/welcome")}>
          ←
        </button>
        <button className="text-btn">Help</button>
      </div>

      <div className="content auth-content">
        <h1 className="page-title">Log in with phone number</h1>
        <p className="page-subtitle">
          If you haven&apos;t registered with this phone number, we&apos;ll create a
          new account for you
        </p>

        <div className="phone-input-wrap">
          <div className="country-code">+86 ▾</div>
          <div className="divider" />
          <input
            className="phone-input"
            placeholder="Enter phone number"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
          />
        </div>

        <button className="switch-login-btn">⇄ Log in with password</button>

        <button
          className={`primary-btn ${canLogin ? "" : "disabled"}`}
          onClick={handleLogin}
        >
          Log in
        </button>

        <div className="agree-row">
          <button
            className={`check-circle ${agreed ? "checked" : ""}`}
            onClick={() => setAgreed(!agreed)}
          >
            {agreed ? "✓" : ""}
          </button>
          <p className="agree-text">
            I have read and agree to the Terms, Privacy Policy, Rules Regarding the
            Protection of Personal Information of Minors
          </p>
        </div>

        <div className="social-row">
          <button className="social-circle"></button>
          <button className="social-circle">W</button>
          <button className="social-circle">Q</button>
          <button className="recover-btn">Recover account</button>
        </div>
      </div>
    </div>
  );
}