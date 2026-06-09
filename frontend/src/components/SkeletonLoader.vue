<template>
  <div class="skeleton-container" :class="{ 'skeleton-animate': animate }">
    <div v-for="i in lines" :key="i" class="skeleton-line" :style="{ width: getLineWidth(i) }"></div>
  </div>
</template>

<script setup>
const props = defineProps({
  lines: {
    type: Number,
    default: 3
  },
  animate: {
    type: Boolean,
    default: true
  }
})

const getLineWidth = (index) => {
  if (index === props.lines) return '60%'
  return '100%'
}
</script>

<style scoped>
.skeleton-container {
  padding: 10px 0;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  border-radius: 4px;
  margin-bottom: 12px;
}

.skeleton-animate .skeleton-line {
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
