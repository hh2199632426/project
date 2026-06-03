import { gameConfig } from "../config/gameConfig";
import { http } from "./http";

export const login = (username, password) => {
  return http.post(gameConfig.api.loginUrl, {
    username,
    password
  });
};
