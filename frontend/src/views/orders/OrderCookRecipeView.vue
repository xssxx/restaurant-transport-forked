<script setup>
import Sidebar from '@/components/Sidebar.vue'
import receiptApi from '@/api/receiptApi.js'
import recipeApi from '@/api/recipeApi.js'
import router from '@/router/index.js'
import { useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'

const b_id = ref('')
const username = ref('')
const foodList = ref([])
const recipeList = ref({})

const goBack = () => {
    router.push('/orderforcook')
}

onMounted(async () => {
    try {
        const route = useRoute()
        const { data: responseOrder } = await receiptApi.getReceiptById(
            route.params.id
        )
        const { data: responseUser } = await receiptApi.getUserById(
            route.params.id
        )
        const { data: responseFood } = await receiptApi.getFoodById(
            route.params.id
        )

        b_id.value = responseOrder.data.id
        username.value = responseUser.data.username
        foodList.value = responseFood.data.foods
        // Fetch recipes for each food item
        for (const item of foodList.value) {
            const { data: responseRecipe } = await recipeApi.getRecipeByFoodId(
                item.food.id
            )
            recipeList.value[item.food.id] = responseRecipe.data // Store recipes by food ID
            console.log('Recipe for', item.food.name, ':', responseRecipe.data)
        }
        console.log(recipeList.value)
    } catch (error) {
        console.error('Error fetching receipt:', error)
    }
})
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>

        <main
            class="ml-[14rem] w-full h-full min-h-screen py-4 px-8 flex flex-col bg-[#FDFDFD]"
        >
            <!-- Back button -->
            <div
                class="flex bg-[#C7C7C7FF] text-white rounded-lg px-3 py-2 w-10 mb-4"
                @click="goBack"
            >
                <button>
                    <fa icon="arrow-left" />
                </button>
            </div>

            <div class="flex justify-center h-5/6 relative">
                <!-- Receipt -->
                <div
                    class="w-4/6 p-10 rounded-lg shadow-md border border-gray-300 bg-[#FDFDFD]"
                >
                    <h1 class="text-center text-3xl mb-4">
                        SuperDuper Restaurant
                    </h1>

                    <hr class="my-2 border-1 border-dashed border-gray-400" />

                    <!-- Ingredients -->
                    <div>
                        <ul>
                            <li
                                v-for="item in foodList"
                                :key="item.food.id"
                                class="flex flex-col mb-2"
                            >
                                <p class="font-bold">{{ item.food.name }}</p>
                                <ul class="ml-4">
                                    <li
                                        v-for="ingredient in recipeList[
                                            item.food.id
                                        ]"
                                        :key="ingredient.id"
                                    >
                                        - {{ ingredient.ingredient.name }}
                                        {{ ingredient.qty.toFixed(2) }}
                                        {{ ingredient.ingredient.unit }}
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>

                    <hr class="my-2 border-1 border-dashed border-gray-400" />

                    <!-- Reference -->
                    <div class="flex justify-between mb-2">
                        <p>Ref. ID</p>
                        <p>{{ b_id }}</p>
                    </div>
                    <!-- Customer Name -->
                    <div class="flex justify-between mb-2">
                        <p>Customer Name</p>
                        <p>{{ username }}</p>
                    </div>

                    <hr class="my-2 border-1 border-dashed border-gray-400" />
                </div>
            </div>
        </main>
    </div>
</template>
