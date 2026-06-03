import { http } from './http';

export const getProfile = () => http.get('/api/users/profile');
export const updateProfile = (nickName) => http.put('/api/users/profile', { nickName });
export const changePassword = (oldPassword, newPassword) => http.post('/api/users/change-password', { oldPassword, newPassword });
