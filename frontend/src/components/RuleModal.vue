<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <h2>游戏规则</h2>
      <div class="rules">
        <p>1. 这是一个双人PK赛车答题游戏。</p>
        <p>2. 答对题目加速，答错题目与答题超时减速。</p>
        <p>3. 先到达终点的玩家获胜。</p>
        <p>4. 有多种模式和难度的选择。</p>
      </div>
      <button class="btn" @click="close">我知道了</button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  visible: Boolean
});

const emit = defineEmits(['update:visible']);

const close = () => {
  emit('update:visible', false);
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 400px;
  text-align: center;
}

.rules {
  text-align: left;
  margin: 20px 0;
  line-height: 1.6;
}

.btn {
  padding: 8px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
