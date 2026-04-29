import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getShopDetail } from "../api/shop";
import type { Shop } from "../api/shop";
import BottomNav from "../components/BottomNav";

export default function ShopDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [shop, setShop] = useState<Shop | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadShopDetail();
  }, [id]);

  const loadShopDetail = async () => {
    try {
      const res = await getShopDetail(id || "");

      if (!res.data.success) {
        alert(res.data.errorMsg || "가게 상세 조회 실패");
        return;
      }

      setShop(res.data.data);
    } catch (error) {
      console.error(error);
      alert("서버 연결 실패");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="app-page">가게 정보를 불러오는 중...</div>;
  }

  if (!shop) {
    return (
      <div className="app-page">
        <button type="button" onClick={() => navigate(-1)}>
          뒤로가기
        </button>
        <p>가게 정보가 없습니다.</p>
      </div>
    );
  }

   const imageUrl =
  "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=80";

    return (
    <div className="app-page shop-detail-page">
        <section className="shop-detail-hero">
        <div
            className="shop-detail-hero-bg"
            style={{ backgroundImage: `url(${imageUrl})` }}
        />

        <div className="shop-detail-top-bar">
            <button
            type="button"
            className="shop-detail-back-btn"
            onClick={() => navigate(-1)}
            >
            ‹
            </button>

            <div className="shop-detail-top-actions">
            <button type="button">♡</button>
            <button type="button">↗</button>
            </div>
        </div>

        <div className="shop-detail-main-photo">
            <img src={imageUrl} alt={shop.name} />
            <span>대표 사진</span>
        </div>
        </section>

        <section className="shop-detail-basic">
            <div className="shop-detail-title-row">
                <div>
                <p className="shop-detail-category">
                    {shop.category} · {shop.region}
                </p>
                <h1>{shop.name}</h1>
                </div>

                <div className="shop-detail-rating-box">
                <span>⭐</span>
                <strong>{shop.rating}</strong>
                </div>
            </div>

            <div className="shop-detail-score-row">
                <span>맛 {shop.rating}</span>
                <span>분위기 4.5</span>
                <span>서비스 4.4</span>
            </div>

            <div className="shop-detail-tags">
                <span>인기 맛집</span>
                <span>{shop.category}</span>
                <span>리뷰 {shop.reviewCount}</span>
            </div>

            <div className="shop-detail-divider" />

            <div className="shop-detail-info-list">
                <div className="shop-detail-info-item">
                <span>🕒</span>
                <div>
                    <strong>영업 중</strong>
                    <p>11:00 - 21:30</p>
                </div>
                </div>

                <div className="shop-detail-info-item">
                <span>📍</span>
                <div>
                    <strong>{shop.address}</strong>
                    <p>{shop.region} 근처 인기 가게</p>
                </div>
                </div>

                <div className="shop-detail-info-item">
                <span>💬</span>
                <div>
                    <strong>{shop.description}</strong>
                    <p>평균 가격 ₩{shop.avgPrice.toLocaleString()}</p>
                </div>
                </div>
            </div>

            <div className="shop-detail-action-row">
                <button type="button">전화</button>
                <button type="button">길찾기</button>
                <button type="button">저장</button>
            </div>
            </section>

        <section className="shop-detail-review-section">
            <div className="shop-detail-tab-row">
                <button type="button">쿠폰</button>
                <button type="button">추천 메뉴</button>
                <button type="button" className="active">
                리뷰 ({shop.reviewCount})
                </button>
            </div>

            <div className="shop-detail-review-header">
                <div>
                <h2>방문자 후기</h2>
                <p>사람들이 남긴 먹방 로그를 확인해보세요.</p>
                </div>

                <button type="button" className="shop-detail-write-btn">
                후기 작성
                </button>
            </div>

            <div className="shop-detail-review-list">
                <article className="shop-detail-review-card">
                <div className="shop-detail-review-user">
                    <div className="shop-detail-review-avatar">M</div>
                    <div>
                    <strong>minwoo_log</strong>
                    <p>⭐️⭐️⭐️⭐️⭐️ · 오늘</p>
                    </div>
                </div>

                <p className="shop-detail-review-text">
                    바삭한 식감이 좋고 양도 충분했어요. 점심시간에는 사람이 많아서
                    조금 기다렸지만 다시 방문하고 싶은 맛집입니다.
                </p>

                <div className="shop-detail-review-images">
                    <div />
                    <div />
                    <div />
                </div>

                <div className="shop-detail-review-footer">
                    <span>댓글 2</span>
                    <span>도움돼요 12</span>
                </div>
                </article>

                <article className="shop-detail-review-card">
                <div className="shop-detail-review-user">
                    <div className="shop-detail-review-avatar second">G</div>
                    <div>
                    <strong>guest_foodie</strong>
                    <p>⭐️⭐️⭐️⭐️☆ · 2일 전</p>
                    </div>
                </div>

                <p className="shop-detail-review-text">
                    분위기가 깔끔하고 직원분들도 친절했어요. 가격도 무난해서 친구랑
                    가볍게 먹기 좋았습니다.
                </p>

                <div className="shop-detail-review-footer">
                    <span>댓글 0</span>
                    <span>도움돼요 5</span>
                </div>
                </article>
            </div>
        </section>
        
        <BottomNav />
    </div>
    );
}