import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginApi, sendCodeApi } from "../api/auth";

export default function LoginPage() {
  const navigate = useNavigate();

  const [phone, setPhone] = useState("");
  const [code, setCode] = useState("");
  const [agreed, setAgreed] = useState(true);
  const [loading, setLoading] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);

  const canSendCode = phone.trim() !== "";
  const canLogin = phone.trim() !== "" && code.trim() !== "" && agreed && !loading;

  const handleSendCode = async () => {
    if (!canSendCode) {
      alert("휴대폰 번호를 입력하세요.");
      return;
    }

    try {
      setSendingCode(true);

      const res = await sendCodeApi({
        phone: phone.trim(),
      });

      if (!res.data.success) {
        alert(res.data.errorMsg || "인증번호 요청 실패");
        return;
      }

      alert("인증번호가 생성되었습니다. 백엔드 콘솔 로그를 확인하세요.");
    } catch (error) {
      console.error(error);
      alert("인증번호 요청 중 오류가 발생했습니다.");
    } finally {
      setSendingCode(false);
    }
  };

  const handleLogin = async () => {
    if (!agreed) {
      alert("약관에 동의해야 로그인할 수 있습니다.");
      return;
    }

    if (!phone.trim() || !code.trim()) {
      alert("휴대폰 번호와 인증번호를 입력하세요.");
      return;
    }

    try {
      setLoading(true);

      const res = await loginApi({
        phone: phone.trim(),
        code: code.trim(),
      });

      if (!res.data.success) {
        alert(res.data.errorMsg || "로그인 실패");
        return;
      }

      const { token, userId, nickname } = res.data.data;

      localStorage.setItem("token", token);
      localStorage.setItem("userId", String(userId));
      localStorage.setItem("nickname", nickname);

      navigate("/home");
    } catch (error) {
      console.error(error);
      alert("로그인 요청 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="screen">
      <div className="top-row">
        <button className="icon-btn" onClick={() => navigate("/me")}>
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

        <div className="phone-input-wrap code-input-wrap">
          <input
            className="phone-input"
            placeholder="Enter verification code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
          />
          <button
            className="send-code-btn"
            onClick={handleSendCode}
            disabled={!canSendCode || sendingCode}
          >
            {sendingCode ? "Sending..." : "Send Code"}
          </button>
        </div>

        <button className="switch-login-btn">⇄ Log in with password</button>

        <button
          className={`primary-btn ${canLogin ? "" : "disabled"}`}
          onClick={handleLogin}
          disabled={!canLogin}
        >
          {loading ? "Logging in..." : "Log in"}
        </button>

        <div className="agree-row">
          <button
            className={`check-circle ${agreed ? "checked" : ""}`}
            onClick={() => setAgreed(!agreed)}
            type="button"
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