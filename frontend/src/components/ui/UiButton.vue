<script setup lang="ts">
withDefaults(
  defineProps<{
    variant?: 'primary' | 'ghost' | 'danger'
    size?: 'sm' | 'md'
    loading?: boolean
    disabled?: boolean
    block?: boolean
  }>(),
  { variant: 'ghost', size: 'md', loading: false, disabled: false, block: false },
)
</script>

<template>
  <button
    class="inline-flex items-center justify-center gap-2 rounded-xl font-medium ring-1 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
    :class="[
      block ? 'w-full' : '',
      size === 'sm' ? 'h-9 px-3 text-sm' : 'h-10 px-4 text-sm',
      variant === 'primary'
        ? 'bg-sky-500/15 hover:bg-sky-500/20 ring-sky-400/25 text-slate-100'
        : variant === 'danger'
          ? 'bg-rose-500/15 hover:bg-rose-500/20 ring-rose-400/25 text-slate-100'
          : 'bg-white/5 hover:bg-white/10 ring-white/10 text-slate-100',
    ]"
    :disabled="disabled || loading"
  >
    <span v-if="loading" class="inline-flex h-4 w-4 animate-spin rounded-full border border-white/20 border-t-white/70" />
    <slot />
  </button>
</template>

