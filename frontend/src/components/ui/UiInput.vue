<script setup lang="ts">
defineProps<{
  modelValue: string
  type?: string
  placeholder?: string
  autocomplete?: string
}>()

defineEmits<{
  (e: 'update:modelValue', v: string): void
}>()
</script>

<template>
  <input
    :value="modelValue"
    :type="type ?? 'text'"
    :placeholder="placeholder"
    :autocomplete="autocomplete"
    class="mt-1 w-full rounded-xl bg-slate-950/40 ring-1 ring-white/10 px-3 py-2 text-sm outline-none focus:ring-sky-400/40 placeholder:text-slate-600"
    @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
  />
</template>

