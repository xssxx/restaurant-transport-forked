<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import foodApi from '@/api/foodApi.js'
import ingredientApi from '@/api/ingredientApi.js'
import router from '@/router/index.js'
import axios from 'axios'

const foodData = ref({ name: '', price: 0, status: 'AVAILABLE', recipes: [] })
const imageFile = ref<File | null>(null)
const ingredients = ref([])

const imagePreview = computed(() =>
    imageFile.value ? URL.createObjectURL(imageFile.value) : ''
)

const fetchIngredients = async () => {
    try {
        const { data } = await ingredientApi.getIngredient()
        ingredients.value = data.data
    } catch (error) {
        console.error('Error fetching ingredients:', error)
    }
}

onMounted(fetchIngredients)

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement
    if (!target.files || target.files.length === 0) return

    const file = target.files[0]
    const timestamp = Date.now()
    const newFileName = `${timestamp}_${file.name}`

    // Clone the file correctly
    const newFile = new File([file], newFileName, { type: file.type })

    imageFile.value = newFile

    console.log('Updated file:', imageFile.value)
}

const createIngredientMap = () =>
    ingredients.value
        .filter(({ quantity }) => quantity > 0)
        .map(({ id, quantity }) => ({ id, quantity }))

const createFood = async () => {
    if (!imageFile.value) return alert('Please upload an image.')
    if (!ingredients.value.some(({ quantity }) => quantity > 0))
        return alert('Please select at least one ingredient.')

    const formData = new FormData()
    formData.append(
        'food',
        new Blob([JSON.stringify(foodData.value)], { type: 'application/json' })
    )
    formData.append('image', imageFile.value)
    formData.append(
        'ingredients',
        new Blob([JSON.stringify({ ingredientMap: createIngredientMap() })], {
            type: 'application/json',
        })
    )

    try {
        await foodApi.createFood(formData)
        await axios.post('http://localhost:8000/upload', formData)

        alert('Create food successfully')
        router.push('/food')
    } catch (error) {
        alert(
            `Failed to add food: ${
                error.response?.data?.message || error.message
            }`
        )
    }
}

const validateInput = (ingredient) => {
    ingredient.quantity = Math.max(
        0,
        Math.min(ingredient.quantity, ingredient.qty)
    )
}

const checkPriceRange = () => {
    foodData.value.price = Math.max(0, Math.min(foodData.value.price, 1000))
}

const isFormValid = computed(
    () =>
        foodData.value.name.trim() &&
        foodData.value.price > 0 &&
        imageFile.value &&
        ingredients.value.some(({ quantity }) => quantity > 0)
)
</script>

<template>
    <div class="flex">
        <aside class="fixed"><Sidebar /></aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 min-h-screen"
        >
            <h1 class="text-2xl font-bold">Create Food</h1>
            <form @submit.prevent="createFood" class="space-y-4">
                <input
                    v-model="foodData.name"
                    placeholder="Food Name"
                    required
                    class="border p-2 rounded w-full"
                />
                <input
                    v-model.number="foodData.price"
                    type="number"
                    min="0"
                    max="1000"
                    step="0.01"
                    @input="checkPriceRange"
                    required
                    class="border p-2 rounded w-full"
                />
                <label class="font-semibold">Status:</label>
                <select
                    v-model="foodData.status"
                    class="border p-2 rounded w-full"
                >
                    <option value="AVAILABLE">Available</option>
                    <option value="OUT_OF_STOCK">Out of Stock</option>
                </select>
                <label class="font-semibold">Upload Food Image:</label>
                <input
                    type="file"
                    @change="handleFileChange"
                    accept="image/*"
                    required
                    class="border p-2 rounded w-full"
                />
                <img
                    v-if="imagePreview"
                    :src="imagePreview"
                    class="w-32 h-32 object-cover mt-4 rounded"
                />
                <label class="font-semibold">Select Ingredients:</label>
                <div v-if="ingredients.length" class="space-y-2">
                    <div
                        v-for="ingredient in ingredients"
                        :key="ingredient.id"
                        class="flex items-center gap-2"
                    >
                        <input
                            type="number"
                            v-model.number="ingredient.quantity"
                            min="0"
                            :max="ingredient.qty"
                            step="0.01"
                            @input="validateInput(ingredient)"
                            class="border p-2 rounded w-20"
                        />
                        <span class="text-sm"
                            >{{ ingredient.name }} ({{
                                ingredient.price
                            }}$)</span
                        >
                    </div>
                </div>
                <button
                    type="submit"
                    :disabled="!isFormValid"
                    class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 disabled:bg-gray-400"
                >
                    Create Food
                </button>
            </form>
        </main>
    </div>
</template>
