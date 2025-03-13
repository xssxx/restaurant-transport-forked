<template>
    <div
        class="border rounded-lg shadow-md bg-white w-56 h-72 cursor-pointer hover:opacity-50 duration-200"
    >
        <img class="rounded-t-lg h-36 w-full" :src="ingredientData.imagePath" />
        <div class="p-3">
            <h3 class="font-bold text-lg">{{ ingredientData.name }}</h3>
            <p>
                Quantity: {{ ingredientData.qty.toFixed(2) }}
                {{ ingredientData.unit }}.
            </p>
            <p>Status: {{ ingredientData.status }}</p>

            <p
                :style="{
                    color: isExpired(ingredientData.expireDate)
                        ? 'red'
                        : 'black',
                }"
            >
                ExpireDate: {{ ingredientData.expireDate }}
            </p>
        </div>
    </div>
</template>

<script setup>
import userApi from '@/api/userApi.js'
import { onMounted, ref } from 'vue'

// Helper function to check if the ingredient is expired
const isExpired = (date) => {
    const today = new Date()
    const expireDate = new Date(date)
    console.log(today, expireDate)
    return expireDate < today
}

const props = defineProps({
    ingredientData: {
        type: Object,
        required: true,
    },
})

const role = ref('')

onMounted(async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.role
})
</script>
