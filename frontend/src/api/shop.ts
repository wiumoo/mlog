import request from "./request";

export interface Shop {
  id: number;
  name: string;
  category: string;
  address: string;
  region: string;
  rating: number;
  reviewCount: number;
  avgPrice: number;
  imageUrl: string;
  description: string;
  status: number;
  createdAt: string;
  updatedAt: string;
}

export const getShopList = () => {
  return request.get("/shop/list");
};