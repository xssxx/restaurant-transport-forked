<script setup>
import { ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import ingredientApi from '@/api/ingredientApi.js'
import router from '@/router/index.js'

// Utility function to format the date to dd-mm-yy
const formatDateToDDMMYY = (dateStr) => {
    const date = new Date(dateStr)
    const day = String(date.getDate()).padStart(2, '0')
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const year = String(date.getFullYear()) // Last two digits of the year
    return `${day}-${month}-${year}`
}

const ingredientData = ref({
    name: '',
    price: 0.0,
    status: 'AVAILABLE',
    qty: 0.0,
    unit: '',
    expireDate: '',
})

const imageFile = ref(null)
const imagePreview = ref('')

// Handle file upload and preview
const handleFileUpload = (event) => {
    const file = event.target.files?.[0]
    if (file) {
        imageFile.value = file
        imagePreview.value = URL.createObjectURL(file)
    }
}

// Submit ingredient form
const submitIngredient = async () => {
    if (!imageFile.value) {
        alert('Please upload an image.')
        return
    }

    // Format expireDate before submitting
    ingredientData.value.expireDate = formatDateToDDMMYY(
        ingredientData.value.expireDate
    )

    try {
        const formData = new FormData()
        formData.append(
            'ingredient',
            new Blob([JSON.stringify(ingredientData.value)], {
                type: 'application/json',
            })
        )
        formData.append('image', imageFile.value)

        await ingredientApi.createIngredient(formData)
        alert('Ingredient added successfully!')
        router.push('/ingredient')
    } catch (error) {
        console.error('Error adding ingredient:', error.response?.data)
        alert(
            `Failed to add ingredient: ${
                error.response?.data?.message || error.message
            }`
        )
    }
}

// Validate price range
const checkPriceRange = () => {
    if (ingredientData.value.price > 1000.0) {
        ingredientData.value.price = 1000.0
    } else if (ingredientData.value.price < 0.0) {
        ingredientData.value.price = 0.0
    }
}

const checkQtyRange = () => {
    if (ingredientData.value.qty > 10000.0) {
        ingredientData.value.qty = 10000.0
    } else if (ingredientData.value.qty < 0.0) {
        ingredientData.value.qty = 0.0
    }
}

// Navigate back
const goBack = () => {
    router.push('/ingredient')
}
</script>

<template>
    <div class="flex">
        <aside class="fixed"><Sidebar /></aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex-col gap-4 bg-gray-50 h-full min-h-screen"
        >
            <div
                class="flex bg-[#C7C7C7FF] text-white rounded-lg px-3 py-2 w-10 mb-4"
                @click="goBack"
            >
                <button>
                    <fa icon="arrow-left" />
                </button>
            </div>
            <section
                class="items-center justify-center flex-col w-10/12 h-12 mx-auto"
            >
                <h2 class="text-4xl font-bold mb-4">Add New Ingredient</h2>
                <form
                    @submit.prevent="submitIngredient"
                    class="w-full p-10 rounded-lg shadow-md border border-gray-300 bg-[#FDFDFD]"
                >
                    <div>
                        <label for="name" class="block font-semibold mb-1 pt-3"
                            >Name</label
                        >
                        <input
                            id="name"
                            v-model="ingredientData.name"
                            required
                            class="border p-2 rounded w-full"
                        />
                    </div>

                    <div>
                        <label for="price" class="block font-semibold mb-1 pt-3"
                            >Price (ต่อ 1 หน่วย)</label
                        >
                        <input
                            type="number"
                            id="price"
                            v-model.number="ingredientData.price"
                            min="0"
                            max="1000"
                            step="0.01"
                            @input="checkPriceRange()"
                            required
                            class="border p-2 rounded w-full"
                        />
                    </div>

                    <div>
                        <label for="qty" class="block font-semibold mb-1 pt-3"
                            >Quantity</label
                        >
                        <input
                            type="number"
                            id="qty"
                            v-model.number="ingredientData.qty"
                            required
                            @input="checkQtyRange()"
                            min="0"
                            max="10000"
                            step="0.01"
                            class="border p-2 rounded w-full"
                        />
                    </div>

                    <div class="space-y-2">
                        <label
                            for="unit"
                            class="block font-semibold text-gray-700"
                        >
                            Unit
                        </label>
                        <select
                            v-model="ingredientData.unit"
                            id="unit"
                            class="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-400 focus:border-blue-400 transition"
                        >
                            <option value="GRAM">Gram</option>
                            <option value="MILLIGRAM">Milligram</option>
                            <option value="LITER">Liter</option>
                            <option value="MILLILITER">Milliliter</option>
                            <option value="TABLE_SPOON">Table spoon</option>
                            <option value="TEA_SPOON">Tea spoon</option>
                        </select>
                    </div>

                    <div>
                        <label
                            for="status"
                            class="block font-semibold mb-1 pt-3"
                            >Status</label
                        >
                        <select
                            id="status"
                            v-model="ingredientData.status"
                            class="border p-2 rounded w-full"
                        >
                            <option value="AVAILABLE">Available</option>
                            <option value="OUT_OF_STOCK">Out of Stock</option>
                        </select>
                    </div>

                    <div>
                        <label
                            for="expireDate"
                            class="block font-semibold mb-1 pt-3"
                            >Expiration Date</label
                        >
                        <input
                            type="date"
                            id="expireDate"
                            v-model="ingredientData.expireDate"
                            required
                            class="border p-2 rounded w-full"
                        />
                    </div>

                    <div>
                        <label for="image" class="block font-semibold mb-1 pt-3"
                            >Upload Image</label
                        >
                        <input
                            type="file"
                            id="image"
                            @change="handleFileUpload"
                            accept="image/*"
                            class="border p-2 rounded w-full"
                        />
                    </div>

                    <div v-if="imagePreview" class="mt-4">
                        <img
                            :src="imagePreview"
                            alt="Image Preview"
                            class="w-32 h-32 object-cover"
                        />
                    </div>

                    <div class="flex items-center justify-center">
                        <button
                            type="submit"
                            class="w-3/6 mt-4 bg-yellow-300 p-2 rounded shadow-md"
                            @mouseover="
                                event.target.style.transform = 'scale(1.05)'
                            "
                            @mouseout="
                                event.target.style.transform = 'scale(1)'
                            "
                        >
                            Add Ingredient
                        </button>
                    </div>
                </form>
            </section>
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
    color: black;
    border: none;
    cursor: pointer;
}
</style>
