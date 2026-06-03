import axios from "axios";

export const http = axios.create();

const getModeFromUrl = (url) => {
  if (!url) return "user";
  return url.includes("/admin") ? "admin" : "user";
};

http.interceptors.request.use((config) => {
  const url = config?.url || "";
  const mode = getModeFromUrl(url);
  const tokenKey = mode === "admin" ? "adminToken" : "userToken";
  const token = localStorage.getItem(tokenKey);

  if (token && !url.endsWith("/login")) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    const url = error?.config?.url || "";
    const mode = getModeFromUrl(url);

    if (status === 401) {
      if (mode === "admin") {
        localStorage.removeItem("adminToken");
        localStorage.removeItem("adminUser");
        localStorage.removeItem("adminLastActivityAt");
      } else {
        localStorage.removeItem("userToken");
        localStorage.removeItem("user");
        localStorage.removeItem("userLastActivityAt");
      }
      window.dispatchEvent(new CustomEvent("auth:unauthorized", { detail: { mode } }));
    }

    return Promise.reject(error);
  }
);

