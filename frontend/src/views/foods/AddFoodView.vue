<script setup>
import { ref, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import foodApi from '@/api/foodApi.js'
import ingredientApi from '@/api/ingredientApi.js'
import router from '@/router/index.js'

const foodData = ref({
    name: '',
    price: 0.0,
    status: 'AVAILABLE',
    recipes: [],
})

const imageFile = ref(null)
const imagePreview = ref('')
const ingredients = ref([])

const fetchIngredients = async () => {
    try {
        const { data: response } = await ingredientApi.getIngredient()
        ingredients.value = response.data
    } catch (error) {
        console.error('Error fetching ingredients:', error)
    }
}

onMounted(() => {
    fetchIngredients()
})

// Handle image file upload
const handleFileUpload = (event) => {
    const file = event.target.files[0]
    if (file) {
        imageFile.value = file
        imagePreview.value = URL.createObjectURL(file)
    }
}

// Create the ingredient map for the API request
const createIngredientMap = () => {
    return ingredients.value
        .filter((ingredient) => ingredient.quantity > 0) // Only include selected ingredients
        .map((ingredient) => ({
            id: ingredient.id,
            quantity: ingredient.quantity,
        }))
}

// Handle food creation
// Handle food creation
const createFood = async () => {
    try {
        if (!imageFile.value) {
            alert('Please upload an image.')
            return
        }

        // Check if at least one ingredient has a quantity greater than 0
        const hasValidIngredient = ingredients.value.some(
            (ingredient) => ingredient.quantity > 0
        )
        if (!hasValidIngredient) {
            alert(
                'Please select at least one ingredient with a quantity greater than 0.'
            )
            return
        }

        const formData = new FormData()
        // Create FormData to handle both the food data and the image
        formData.append(
            'food',
            new Blob([JSON.stringify(foodData.value)], {
                type: 'application/json',
            })
        )
        formData.append('image', imageFile.value) // Append the image file

        const ingredientsObject = JSON.stringify({
            ingredientMap: createIngredientMap(),
        })

        formData.append(
            'ingredients',
            new Blob([ingredientsObject], {
                // Wrap the ingredientsObject in an array
                type: 'application/json',
            })
        )

        const { data: response } = await foodApi.createFood(formData)
        console.log('Food created successfully:', response.data)
        // Optionally reset form or navigate to another page
        alert('Create food successfully')
        router.push('/food')
    } catch (error) {
        console.error('Error adding food:', error.response?.data)
        alert(
            `Failed to add food: ${
                error.response?.data?.message || error.message
            }`
        )
    }
}

// Validate ingredient quantity input
const validateInput = (ingredient) => {
    if (ingredient.quantity < 0.0) {
        ingredient.quantity = 0.0 // Prevent negative input
    } else if (ingredient.quantity > ingredient.qty) {
        ingredient.quantity = ingredient.qty // Set to max if over limit
    }
}

const checkPriceRange = () => {
    if (foodData.value.price > 1000.0) {
        foodData.value.price = 1000.0
    } else if (foodData.value.price < 0.0) {
        foodData.value.price = 0.0
    }
}
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 h-full min-h-screen"
        >
            <h1>Create Food</h1>
            <form @submit.prevent="createFood">
                <input
                    v-model="foodData.name"
                    placeholder="Food Name"
                    required
                />
                <input
                    v-model.number="foodData.price"
                    type="number"
                    min="0"
                    max="1000"
                    step="0.01"
                    @input="checkPriceRange()"
                    placeholder="Price"
                    required
                />
                <div>
                    <label for="status" class="block font-semibold mb-1"
                        >Status:</label
                    >
                    <select
                        id="status"
                        v-model="foodData.status"
                        class="border p-2 rounded w-full"
                    >
                        <option value="AVAILABLE">Available</option>
                        <option value="OUT_OF_STOCK">Out_of_Stock</option>
                    </select>
                </div>
                <!-- Image upload -->
                <label>Upload Food Image:</label>
                <input
                    type="file"
                    @change="handleFileUpload"
                    accept="image/*"
                    required
                />
                <div v-if="imagePreview" class="mt-4">
                    <img
                        :src="imagePreview"
                        alt="Image Preview"
                        class="w-32 h-32 object-cover"
                    />
                </div>

                <!-- Select ingredients -->
                <label>Select Ingredients:</label>
                <div v-if="ingredients.length">
                    <div
                        v-for="ingredient in ingredients"
                        :key="ingredient.id"
                        class="flex items-center"
                    >
                        <input
                            type="number"
                            v-model.number="ingredient.quantity"
                            min="0"
                            :max="ingredient.qty"
                            step="0.01"
                            @input="validateInput(ingredient)"
                        />
                        <span
                            >{{ ingredient.name }} ({{
                                ingredient.price
                            }}$)</span
                        >
                    </div>
                </div>
                <div v-else>
                    <p>Loading ingredients...</p>
                </div>

                <button type="submit">Create Food</button>
            </form>
        </main>
    </div>
</template>

<style scoped>
input {
    display: block;
    margin-bottom: 10px;
}

button {
    padding: 8px 16px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background-color: #45a049;
}
</style>
