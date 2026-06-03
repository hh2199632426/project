import { http } from './http';

export const getCheckInStatus = () => http.get('/api/checkin/status');
export const submitCheckIn = (success) => http.post('/api/checkin/submit', { success });
