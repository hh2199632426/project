<template>
  <div class="cards">
    <div class="card">
      <div class="label">题目总条数</div>
      <div class="value">{{ adminStore.overview.questionCount }}</div>
    </div>
    <div class="card">
      <div class="label">用户个数</div>
      <div class="value">{{ adminStore.overview.userCount }}</div>
    </div>
    <div class="card">
      <div class="label">目录总条数</div>
      <div class="value">{{ adminStore.overview.categoryCount }}</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useAdminStore } from "../../store/adminStore";

const adminStore = useAdminStore();

onMounted(async () => {
  await adminStore.fetchOverview();
});
</script>

<style scoped>
.cards {
  display: grid;
  grid-template-columns: repeat(3, minmax(200px, 1fr));
  gap: 16px;
}

.card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.label {
  color: #909399;
  margin-bottom: 10px;
}

.value {
  font-size: 30px;
  color: #303133;
  font-weight: 700;
}
</style>
