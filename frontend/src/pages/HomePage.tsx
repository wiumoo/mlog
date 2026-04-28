import "../styles.css";
import BottomNav from "../components/BottomNav";

const categories = ["전체", "한식", "중식", "일식", "카페", "분식", "치킨"];

const posts = [
  {
    id: 1,
    title: "성수동 분위기 좋은 파스타집 다녀왔어요",
    user: "minwoo",
    likes: 136,
    image: "https://images.unsplash.com/photo-1555396273-367ea4eb4db5",
  },
  {
    id: 2,
    title: "홍대 라멘 맛집, 국물이 진짜 깊어요",
    user: "foodie",
    likes: 912,
    image: "https://images.unsplash.com/photo-1569718212165-3a8278d5f624",
  },
  {
    id: 3,
    title: "강남 카페 디저트 후기",
    user: "coffee",
    likes: 88,
    image: "https://images.unsplash.com/photo-1554118811-1e0d58224f24",
  },
  {
    id: 4,
    title: "삼겹살은 역시 불판이 중요함",
    user: "meatlover",
    likes: 320,
    image: "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd",
  },
];

export default function HomePage() {
  return (
    <div className="app-page">
      <header className="top-header">
        <button className="icon-button">☰</button>
        <div className="top-tabs">
          <span>Following</span>
          <span className="active">Explore</span>
          <span>Nearby</span>
        </div>
        <button className="icon-button">⌕</button>
      </header>

      <nav className="category-tabs">
        {categories.map((category, index) => (
          <button key={category} className={index === 0 ? "active" : ""}>
            {category}
          </button>
        ))}
      </nav>

      <main className="masonry-grid">
        {posts.map((post) => (
          <article key={post.id} className="post-card">
            <img src={post.image} alt={post.title} />
            <div className="post-content">
              <h3>{post.title}</h3>
              <div className="post-meta">
                <span>@{post.user}</span>
                <span>♡ {post.likes}</span>
              </div>
            </div>
          </article>
        ))}
      </main>

      <BottomNav />
    </div>
  );
}
