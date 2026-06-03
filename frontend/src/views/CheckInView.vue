<template>
  <div class="checkin-container">
    <div class="header">
      <h2>每日打卡</h2>
      <button class="back-btn" @click="$emit('back')">返回大厅</button>
    </div>
    
    <div class="content">
      <Transition name="fade-slide" mode="out-in">
        <div v-if="!isChallenging && !challengeEnded" key="status">
          <div class="checkin-status">
            <h3>每日打卡</h3>
            <p>累计打卡 {{ checkinStatus.totalCheckinDays }} 天</p>
            <button class="challenge-btn" :disabled="checkinStatus.hasCheckedIn || loading" @click="startChallenge">
              {{ checkinStatus.hasCheckedIn ? '今日已打卡' : (loading ? '题目加载中...' : '开始打卡挑战') }}
            </button>
          </div>
        </div>

        <div v-else-if="isChallenging" key="challenge" class="challenge-area">
          <Transition name="fade-slide" mode="out-in">
            <div class="question-card" :key="currentQuestionIndex" v-if="currentQuestion">
               <div class="progress">题目 {{ currentQuestionIndex + 1 }} / {{ questions.length }}</div>
               <h3 class="question-text">{{ currentQuestion.questionText }}</h3>
               <div class="options">
                 <button v-for="([key, option]) in optionEntries" :key="key"
                         class="option-btn" 
                         :class="getOptionClass(option)"
                         :disabled="selectedThisQuestion"
                         @click="selectOption(option)">
                   {{ key }}. {{ option }}
                 </button>
               </div>
            </div>
          </Transition>
        </div>

        <div v-else-if="challengeEnded" key="result" class="result-area">
          <div class="result-card">
            <h3>打卡挑战结束</h3>
            <div class="result-score">正确率：<span>{{ ((correctCount / questions.length) * 100).toFixed(0) }}%</span></div>
            <div class="result-msg" :class="passChallenge ? 'success' : 'fail'">
              {{ passChallenge ? '恭喜打卡成功！' : '很遗憾，正确率未达标，请重来' }}
            </div>
            <button class="return-btn" @click="returnToStatus">返回打卡页面</button>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Unlock Medal Modal -->
    <div v-if="showUnlockModal && unlockedMedal" class="medal-modal-overlay">
      <div class="medal-modal">
        <h3>🎉 恭喜获得新勋章 🎉</h3>
        <div class="unlocked-medal-circle" :style="{ backgroundColor: unlockedMedal.bgColor }">
          <svg width="100" height="100" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" v-html="unlockedMedal.svgContent"></svg>
        </div>
        <div class="unlocked-medal-name">{{ unlockedMedal.name }}</div>
        <div class="unlocked-medal-desc">{{ unlockedMedal.description }}</div>
        <button class="view-medal-btn" @click="viewMedal">前往查看</button>
        <button class="close-modal-btn" @click="closeUnlockModal">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUiStore } from '../store/uiStore';
import { getCheckInStatus, submitCheckIn } from '../api/checkInApi';
import { getQuestionPool } from '../api/questionApi';
import { gameConfig } from '../config/gameConfig';
import { medals } from '../config/medalConfig';

const uiStore = useUiStore();

const checkinStatus = ref({ hasCheckedIn: false, totalCheckinDays: 0 });
const isChallenging = ref(false);
const challengeEnded = ref(false);
const passChallenge = ref(false);
const questions = ref([]);
const currentQuestionIndex = ref(0);
const correctCount = ref(0);
const loading = ref(false);

const selectedThisQuestion = ref(false);
const currentSelectedOption = ref(null);

const showUnlockModal = ref(false);
const unlockedMedal = ref(null);

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value]);
const optionEntries = computed(() => {
  const opts = currentQuestion.value?.options;
  if (!opts) return [];
  return Object.entries(opts).filter(([, value]) => value);
});

onMounted(async () => {
  try {
    const statusRes = await getCheckInStatus();
    if (statusRes.data) {
      checkinStatus.value = statusRes.data;
    }
  } catch (e) {
    console.error("Failed to load check-in status", e);
  }
});

const startChallenge = async () => {
  loading.value = true;
  try {
    const res = await getQuestionPool(gameConfig.modes.checkin.questionCount);
    if (res.data && res.data.length > 0) {
      questions.value = res.data.map(q => {
        const opts = Array.isArray(q.options)
          ? q.options
          : [q.optionA, q.optionB, q.optionC, q.optionD].filter(Boolean);

        return {
          id: q.id,
          questionText: q.text ?? q.questionText,
          options: {
            A: opts[0],
            B: opts[1],
            C: opts[2],
            D: opts[3]
          },
          correctAnswer: q.answer ?? q.correctAnswer
        };
      });
      currentQuestionIndex.value = 0;
      correctCount.value = 0;
      isChallenging.value = true;
      challengeEnded.value = false;
      passChallenge.value = false;
      selectedThisQuestion.value = false;
      currentSelectedOption.value = null;
    } else {
      uiStore.addToast('题目加载失败，请稍后重试', 'error');
    }
  } catch (e) {
    uiStore.addToast('题目加载失败', 'error');
  } finally {
    loading.value = false;
  }
};

const getOptionClass = (option) => {
  if (!selectedThisQuestion.value) return '';
  if (option === currentQuestion.value.correctAnswer) {
    return 'correct-option';
  }
  if (option === currentSelectedOption.value && option !== currentQuestion.value.correctAnswer) {
    return 'wrong-option';
  }
  return '';
};

const selectOption = async (selectedOption) => {
  if (selectedThisQuestion.value) return;
  
  selectedThisQuestion.value = true;
  currentSelectedOption.value = selectedOption;

  if (selectedOption === currentQuestion.value.correctAnswer) {
    correctCount.value++;
    // uiStore.addToast('回答正确', 'success');
  } else {
    // uiStore.addToast('回答错误', 'error');
  }
  
  // 延迟 800ms 以展示颜色变化，再进行跳转
  setTimeout(async () => {
    if (currentQuestionIndex.value < questions.value.length - 1) {
      currentQuestionIndex.value++;
      selectedThisQuestion.value = false;
      currentSelectedOption.value = null;
    } else {
      // Finish challenge
      const total = questions.value.length;
      const rate = correctCount.value / total;
      isChallenging.value = false;
      challengeEnded.value = true;
      
      if (rate >= gameConfig.modes.checkin.passRate) {
        passChallenge.value = true;
        try {
          await submitCheckIn(true);
          checkinStatus.value.hasCheckedIn = true;
          checkinStatus.value.totalCheckinDays++;
        } catch (e) {
          const msg = e?.response?.data?.message || '打卡提交失败，请稍后重试';
          uiStore.addToast(msg, 'error');
        }
      } else {
        passChallenge.value = false;
      }
    }
  }, 800);
};

const returnToStatus = async () => {
  if (passChallenge.value) {
    // Check if a medal is unlocked
    try {
      const statusRes = await getCheckInStatus();
      if (statusRes.data) {
        checkinStatus.value = statusRes.data;
        const totalDays = checkinStatus.value.totalCheckinDays;
        
        // Find if any medal is unlocked exactly today
        const unlocked = medals.find(m => m.requiredDays === totalDays);
        if (unlocked) {
          unlockedMedal.value = unlocked;
          showUnlockModal.value = true;
          // Don't close challenge yet, modal is over it or replace it
          // Actually we can close challenge view now
        }
      }
    } catch (e) {
      console.error("Failed to check medal status", e);
    }
  }
  
  challengeEnded.value = false;
  isChallenging.value = false;
};

const closeUnlockModal = () => {
  showUnlockModal.value = false;
  unlockedMedal.value = null;
};

const viewMedal = () => {
  if (unlockedMedal.value) {
    emit('view-medal', unlockedMedal.value.id);
  }
  closeUnlockModal();
};

const emit = defineEmits(['back', 'view-medal']);
</script>

<style scoped>
.checkin-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
  padding: 40px;
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

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.checkin-status {
  text-align: center;
}

.checkin-status h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

.checkin-status p {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.challenge-btn {
  padding: 12px 30px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.challenge-btn:hover:not(:disabled) {
  background-color: #73d13d;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.challenge-btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
  color: #999;
}

.challenge-area {
  width: 100%;
  max-width: 600px;
}

.question-card {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.progress {
  text-align: right;
  color: #999;
  margin-bottom: 10px;
}

.question-text {
  margin-bottom: 20px;
  font-size: 18px;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-btn {
  padding: 12px;
  text-align: left;
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-btn:hover:not(:disabled) {
  background: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
}

.correct-option {
  background-color: #f6ffed !important;
  border-color: #52c41a !important;
  color: #52c41a !important;
}

.wrong-option {
  background-color: #fff2f0 !important;
  border-color: #ff4d4f !important;
  color: #ff4d4f !important;
}

.option-btn:disabled {
  cursor: default;
}

/* Result styles */
.result-area {
  width: 100%;
  max-width: 400px;
}

.result-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  text-align: center;
}

.result-card h3 {
  margin-top: 0;
  font-size: 24px;
  color: #333;
}

.result-score {
  font-size: 18px;
  margin: 20px 0;
  color: #666;
}

.result-score span {
  font-size: 32px;
  font-weight: bold;
  color: #1890ff;
}

.result-msg {
  font-size: 18px;
  margin-bottom: 30px;
}

.result-msg.success {
  color: #52c41a;
}

.result-msg.fail {
  color: #ff4d4f;
}

.return-btn {
  padding: 10px 24px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.return-btn:hover {
  background-color: #40a9ff;
}

/* Transition animations */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* Medal Unlock Modal */
.medal-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(5px);
}

.medal-modal {
  background: white;
  padding: 40px;
  border-radius: 20px;
  text-align: center;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.medal-modal h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
}

.unlocked-medal-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 20px;
  animation: shake 2s ease-in-out infinite, glow 2s ease-in-out infinite;
}

.unlocked-medal-name {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.unlocked-medal-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.view-medal-btn {
  display: block;
  width: 100%;
  padding: 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  margin-bottom: 15px;
  transition: background-color 0.3s;
}

.view-medal-btn:hover {
  background-color: #40a9ff;
}

.close-modal-btn {
  background: transparent;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 14px;
  text-decoration: underline;
}

@keyframes popIn {
  from { opacity: 0; transform: scale(0.5); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-5deg); }
  75% { transform: rotate(5deg); }
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 15px rgba(255, 215, 0, 0.5); }
  50% { box-shadow: 0 0 30px rgba(255, 215, 0, 0.8), 0 0 50px rgba(255, 165, 0, 0.6); }
}
</style>
