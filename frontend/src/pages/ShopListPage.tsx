import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getShopList } from "../api/shop";
import type { Shop } from "../api/shop";
import BottomNav from "../components/BottomNav";
import "../styles.css";

const categories = ["전체", "한식", "중식", "일식", "양식", "카페", "분식", "치킨"];

export default function ShopListPage() {
  const [shops, setShops] = useState<Shop[]>([]);
  const [selectedCategory, setSelectedCategory] = useState("전체");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadShopList();
  }, []);

  const loadShopList = async () => {
    try {
      const res = await getShopList();

      if (!res.data.success) {
        alert(res.data.errorMsg || "가게 목록 조회 실패");
        return;
      }

      const shopList: Shop[] = res.data.data || [];

      // 현재는 거리순/별점순 미구현 상태이므로 id 오름차순으로 정렬
      const sortedShops = [...shopList].sort((a, b) => a.id - b.id);

      setShops(sortedShops);
    } catch (error) {
      console.error(error);
      alert("서버 연결 실패");
    } finally {
      setLoading(false);
    }
  };

  const filteredShops =
    selectedCategory === "전체"
      ? shops
      : shops.filter((shop) => shop.category === selectedCategory);

  return (
    <div className="app-page">
      <header className="top-header">
        <button className="icon-button">☰</button>

        <div className="top-tabs">
          <span>Distance</span>
          <span className="active">Shop List</span>
          <span>Rating</span>
        </div>

        <button className="icon-button">⌕</button>
      </header>

      <nav className="category-tabs">
        {categories.map((category) => (
          <button
            key={category}
            className={selectedCategory === category ? "active" : ""}
            onClick={() => setSelectedCategory(category)}
          >
            {category}
          </button>
        ))}
      </nav>

      <section className="page-title-header">
        <h1>근처 맛집 리스트</h1>
        <p>현재는 DB에 등록된 가게를 ID 순서대로 보여줍니다.</p>
      </section>

      {loading ? (
        <div className="empty-state">가게 목록을 불러오는 중...</div>
      ) : filteredShops.length === 0 ? (
        <div className="empty-state">해당 카테고리의 가게가 없습니다.</div>
      ) : (
        <main className="shop-list">
          {filteredShops.map((shop) => (
            <article
                key={shop.id}
                className="shop-card"
                role="button"
                tabIndex={0}
                onClick={() => navigate(`/shops/${shop.id}`)}
                onKeyDown={(event) => {
                  if (event.key === "Enter") {
                    navigate(`/shops/${shop.id}`);
                  }
                }}
              >
              <img
                src={
                  shop.imageUrl && !shop.imageUrl.includes("example.com")
                    ? shop.imageUrl
                    : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4"
                }
                alt={shop.name}
              />

              <div className="shop-info">
                <div className="shop-title-row">
                  <h3>{shop.name}</h3>
                  <span className="shop-rank">#{shop.id}</span>
                </div>

                <p>
                  {shop.category} · {shop.region}
                </p>

                <p>{shop.address}</p>

                <div className="shop-meta">
                  <span>⭐ {shop.rating}</span>
                  <span>리뷰 {shop.reviewCount}</span>
                  <span>₩{shop.avgPrice}</span>
                </div>
              </div>
            </article>
          ))}
        </main>
      )}

      <BottomNav />
    </div>
  );
}