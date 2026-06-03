<template>
  <div class="profile-container">
    <div class="sidebar">
      <div class="menu-item" :class="{ active: currentTab === 'info' }" @click="currentTab = 'info'">
        个人中心
      </div>
      <div class="menu-item" :class="{ active: currentTab === 'password' }" @click="currentTab = 'password'">
        修改密码
      </div>
      <div class="menu-item" :class="{ active: currentTab === 'medals' }" @click="currentTab = 'medals'">
        勋章展馆
      </div>
    </div>
    <div class="content">
      <div class="header">
        <h2>{{ getTitle(currentTab) }}</h2>
        <button class="back-btn" @click="$emit('back')">返回大厅</button>
      </div>
      
      <div v-if="currentTab === 'info'" class="tab-content">
        <div class="form-group">
          <label>昵称</label>
          <input type="text" v-model="nickName" placeholder="请输入昵称" />
        </div>
        <button class="save-btn" @click="saveProfile">保存</button>
      </div>

      <div v-if="currentTab === 'password'" class="tab-content">
        <div class="form-group">
          <label>原密码</label>
          <input type="password" v-model="passwordForm.old" placeholder="请输入原密码" />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input type="password" v-model="passwordForm.new" placeholder="请输入新密码" />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input type="password" v-model="passwordForm.confirm" placeholder="请确认新密码" />
        </div>
        <button class="save-btn" @click="savePassword">保存</button>
      </div>

      <div v-if="currentTab === 'medals'" class="tab-content">
        <div class="medals-grid">
          <div v-for="category in medalCategories" :key="category.id" class="medal-item clickable-item" @click="openCategory(category)">
            <div class="medal-circle" :style="{ backgroundColor: category.cover.bgColor }">
              <svg v-if="category.cover.svgContent" width="48" height="48" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" v-html="category.cover.svgContent"></svg>
              <span v-else class="medal-icon">{{ category.cover.icon }}</span>
            </div>
            <div class="medal-name">{{ category.name }}</div>
            <div class="medal-desc">{{ category.description }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 勋章展馆轮播弹窗 -->
    <div v-if="showCarousel" class="carousel-overlay" @click.self="closeCarousel">
      <button class="close-btn" @click="closeCarousel">×</button>
      
      <div class="carousel-container">
        <button class="nav-btn left" @click="prevMedal" :class="{ disabled: currentIndex === 0 }">❮</button>
        
        <div class="carousel-track">
          <div v-for="(medal, index) in currentMedals" :key="medal.id" 
               class="carousel-item" :class="getCarouselClass(index)"
               @click="openCarousel(index)">
            <div class="medal-circle-large" :style="{ backgroundColor: medal.bgColor }" :class="{ 'grayscale': !isMedalUnlocked(medal) }">
              <svg width="120" height="120" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" v-html="medal.svgContent"></svg>
            </div>
            <div class="medal-info-large" :class="{ 'fade-out': index !== currentIndex }">
              <div class="medal-name-large">{{ medal.name }}</div>
              <div class="medal-desc-large">{{ medal.description }}</div>
              <div class="medal-status" :class="isMedalUnlocked(medal) ? 'obtained' : 'unobtained'">
                {{ isMedalUnlocked(medal) ? '已获得' : '未获得' }}
              </div>
            </div>
          </div>
        </div>

        <button class="nav-btn right" @click="nextMedal" :class="{ disabled: currentIndex === currentMedals.length - 1 }">❯</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '../store/userStore';
import { useUiStore } from '../store/uiStore';
import { getProfile } from '../api/userApi';
import { medals } from '../config/medalConfig';

const props = defineProps({
  initialTab: {
    type: String,
    default: 'info'
  },
  initialMedalId: {
    type: Number,
    default: null
  }
});

const userStore = useUserStore();
const uiStore = useUiStore();
const currentTab = ref(props.initialTab || 'info');
const nickName = ref('');
const passwordForm = ref({
  old: '',
  new: '',
  confirm: ''
});

const medalCategories = [
  {
    id: 'checkin',
    name: '打卡勋章',
    description: '累计打卡即可获得',
    cover: medals[0],
    list: medals
  }
];

const showCarousel = ref(false);
const currentIndex = ref(0);
const currentMedals = ref([]);

const openCategory = (category) => {
  currentMedals.value = category.list;
  currentIndex.value = 0;
  showCarousel.value = true;
};

const openCarousel = (index) => {
  currentIndex.value = index;
  showCarousel.value = true;
};

// Handle initial medal opening
onMounted(() => {
  if (props.initialMedalId) {
    const category = medalCategories.find(c => c.list.some(m => m.id === props.initialMedalId));
    if (category) {
      currentMedals.value = category.list;
      const index = category.list.findIndex(m => m.id === props.initialMedalId);
      if (index !== -1) {
        currentIndex.value = index;
        showCarousel.value = true;
      }
    }
  }
});

const closeCarousel = () => {
  showCarousel.value = false;
};

const prevMedal = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};

const nextMedal = () => {
  if (currentIndex.value < currentMedals.value.length - 1) {
    currentIndex.value++;
  }
};

const getCarouselClass = (index) => {
  if (index === currentIndex.value) return 'active';
  if (index === currentIndex.value - 1) return 'prev';
  if (index === currentIndex.value + 1) return 'next';
  if (index < currentIndex.value - 1) return 'hidden-left';
  if (index > currentIndex.value + 1) return 'hidden-right';
  return 'hidden';
};

const isMedalUnlocked = (medal) => {
  const days = userStore.user?.totalCheckinDays || 0;
  return days >= medal.requiredDays;
};

const getTitle = (tab) => {
  const map = {
    'info': '个人中心',
    'password': '修改密码',
    'medals': '勋章展馆'
  };
  return map[tab] || '';
};

onMounted(async () => {
  try {
    const res = await getProfile();
    const data = res?.data;
    if (data && typeof data === "object") {
      userStore.user = data;
      localStorage.setItem("user", JSON.stringify(data));
      nickName.value = data.nickName || '';
    }
  } catch (e) {
    if (userStore.user && typeof userStore.user === "object") {
      nickName.value = userStore.user.nickName || '';
    }
  }
});

const saveProfile = async () => {
  if (!nickName.value.trim()) {
    uiStore.addToast('昵称不能为空', 'warning');
    return;
  }
  const success = await userStore.updateProfile(nickName.value);
  if (success) {
    uiStore.addToast('保存成功', 'success');
  } else {
    uiStore.addToast('保存失败', 'error');
  }
};

const savePassword = async () => {
  if (!passwordForm.value.old || !passwordForm.value.new || !passwordForm.value.confirm) {
    uiStore.addToast('请填写完整信息', 'warning');
    return;
  }
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    uiStore.addToast('两次输入的新密码不一致', 'warning');
    return;
  }
  try {
    await userStore.changePassword(passwordForm.value.old, passwordForm.value.new);
    uiStore.addToast('密码修改成功', 'success');
    passwordForm.value = { old: '', new: '', confirm: '' };
  } catch (error) {
     const msg = error.response?.data?.message || '密码修改失败';
     uiStore.addToast(msg, 'error');
  }
};
</script>

<style scoped>
.profile-container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

.sidebar {
  width: 200px;
  background-color: white;
  padding-top: 20px;
  border-right: 1px solid #e0e0e0;
}

.menu-item {
  padding: 15px 20px;
  cursor: pointer;
  color: #333;
}

.menu-item:hover {
  background-color: #f0f0f0;
}

.menu-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}

.content {
  flex: 1;
  padding: 40px;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.back-btn {
  padding: 8px 16px;
  background-color: transparent;
  color: #666;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
}

.form-group input {
  width: 100%;
  max-width: 400px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.save-btn {
  padding: 10px 30px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-btn:hover {
  background-color: #40a9ff;
}

.medals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 30px;
}

.medal-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.medal-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.medal-circle:hover {
  transform: scale(1.05);
}

.medal-icon {
  font-size: 48px;
}

.medal-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
  color: #333;
}

.medal-desc {
  font-size: 12px;
  color: #999;
}

.clickable-item {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.clickable-item:hover {
  transform: translateY(-5px);
}

/* Carousel Overlay */
.carousel-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.close-btn {
  position: absolute;
  top: 30px;
  right: 40px;
  font-size: 40px;
  color: white;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: transform 0.3s;
}

.close-btn:hover {
  transform: rotate(90deg);
}

.carousel-container {
  position: relative;
  width: 800px;
  height: 400px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.carousel-track {
  position: relative;
  width: 600px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  perspective: 1000px;
}

.carousel-item {
  position: absolute;
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 240px;
}

.carousel-item.active {
  transform: translateX(0) scale(1);
  opacity: 1;
  z-index: 3;
}

.carousel-item.prev {
  transform: translateX(-180px) scale(0.75);
  opacity: 0.4;
  z-index: 2;
  cursor: pointer;
}

.carousel-item.next {
  transform: translateX(180px) scale(0.75);
  opacity: 0.4;
  z-index: 2;
  cursor: pointer;
}

.carousel-item.hidden-left {
  transform: translateX(-300px) scale(0.5);
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.carousel-item.hidden-right {
  transform: translateX(300px) scale(0.5);
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.carousel-item.hidden {
  display: none;
}

.medal-circle-large {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  transition: all 0.5s ease;
}

.medal-info-large {
  text-align: center;
  transition: opacity 0.3s ease;
}

.medal-info-large.fade-out {
  opacity: 0;
}

.medal-name-large {
  font-size: 24px;
  font-weight: bold;
  color: white;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.medal-desc-large {
  font-size: 16px;
  color: #ddd;
}

.medal-status {
  margin-top: 10px;
  font-size: 14px;
  font-weight: bold;
}

.obtained {
  color: #4CAF50;
}

.unobtained {
  color: #999;
}

.grayscale {
  filter: grayscale(100%);
  opacity: 0.6;
}

.nav-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.nav-btn:hover:not(.disabled) {
  background: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
}

.nav-btn.disabled {
  opacity: 0.2;
  cursor: not-allowed;
}
</style>
