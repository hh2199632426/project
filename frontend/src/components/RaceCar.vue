<template>
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 120" :width="width" :height="height">
    <!-- Front Wing (Top) -->
    <path d="M10,15 L90,15 L95,5 L5,5 Z" :fill="secondaryColor" />
    
    <!-- Front Wheels -->
    <rect x="0" y="25" width="15" height="25" fill="#212121" rx="2" />
    <rect x="85" y="25" width="15" height="25" fill="#212121" rx="2" />
    
    <!-- Main Body -->
    <path d="M25,25 L75,25 L80,95 L20,95 Z" :fill="primaryColor" />
    
    <!-- Cockpit -->
    <ellipse cx="50" cy="55" rx="12" ry="15" fill="#333" />
    <circle cx="50" cy="60" r="7" :fill="helmetColor" /> <!-- Helmet -->
    
    <!-- Rear Wheels -->
    <rect x="0" y="80" width="15" height="30" fill="#212121" rx="2" />
    <rect x="85" y="80" width="15" height="30" fill="#212121" rx="2" />
  
    <!-- Rear Wing (Bottom) -->
    <rect x="10" y="100" width="80" height="15" :fill="secondaryColor" rx="2" />
    
    <!-- Stripe -->
    <rect x="46" y="20" width="8" height="85" fill="#ffffff" opacity="0.8" />
  </svg>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  color: {
    type: String,
    default: 'red', // 'red' or 'yellow'
  },
  width: {
    type: [Number, String],
    default: '100%'
  },
  height: {
    type: [Number, String],
    default: 'auto'
  }
});

const primaryColor = computed(() => {
  return props.color === 'yellow' ? '#FFEB3B' : '#f44336';
});

const secondaryColor = computed(() => {
  return props.color === 'yellow' ? '#FBC02D' : '#d32f2f';
});

const helmetColor = computed(() => {
  return props.color === 'yellow' ? '#F44336' : '#ffeb3b';
});
</script>
