<template>
  <div id="app">
    <header class="app-header">
      <span class="app-logo">📝 Java 出题组卷系统</span>
      <nav class="nav-links">
        <router-link to="/papers">试卷列表</router-link>
        <router-link to="/questions">题库管理</router-link>
        <router-link to="/auto-generate">自动组卷</router-link>
      </nav>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>
