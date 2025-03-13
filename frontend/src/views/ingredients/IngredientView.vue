<script setup>
import { ref, onMounted, computed } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import EditIngredientModal from '@/components/modals/EditIngredientModal.vue'
import IngredientCard from '@/components/cards/IngredientCard.vue'
import ingredientApi from '@/api/ingredientApi.js'
import Search from '@/components/Search.vue'
import router from '@/router/index.js'
import userApi from '@/api/userApi.js'

const role = ref('')
const ingredients = ref([])
const searchQuery = ref('')
const isModalOpened = ref(false)
const selectedIngredient = ref({
    id: '',
    name: '',
    qty: 0.0,
    status: '',
    expireDate: '',
})

const openModal = (ingredient) => {
    if (role.value !== 'ADMIN') return
    selectedIngredient.value = { ...ingredient }
    isModalOpened.value = true
}

const closeModal = () => {
    isModalOpened.value = false
    selectedIngredient.value = {
        id: '',
        name: '',
        qty: 0.0,
        status: '',
        expireDate: '',
    } // Reset when modal closes
}

const submitHandler = async () => {
    try {
        // Call API to update the ingredient
        const { data: response } = await ingredientApi.updateIngredient({
            id: selectedIngredient.value.id,
            qty: selectedIngredient.value.qty,
        })
        console.log('Ingredient updated:', response.data)

        // Update the local ingredients array
        const index = ingredients.value.findIndex(
            (i) => i.id === selectedIngredient.value.id
        )
        if (index !== -1) {
            ingredients.value[index] = { ...selectedIngredient.value }
        }

        // Close modal after successful update
        closeModal()
        await fetchIngredients()
    } catch (error) {
        console.error('Error updating ingredient:', error)
    }
}

const filteredIngredients = computed(() => {
    if (!searchQuery.value) return ingredients.value
    return ingredients.value.filter((ingredient) =>
        ingredient.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})

const addingredients = () => {
    router.push('/addingredients')
}

const fetchIngredients = async () => {
    try {
        const { data: response } = await ingredientApi.getIngredient()
        ingredients.value = response.data
        console.log(response.data)
    } catch (error) {
        console.error('Error fetching ingredients:', error)
    }
}

const getRole = async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role
}

onMounted(() => {
    fetchIngredients()
    getRole()
})

// Corrected validateRange function
const validateRange = () => {
    if (selectedIngredient.value.qty > 1000) {
        selectedIngredient.value.qty = 1000
    } else if (selectedIngredient.value.qty < 0) {
        selectedIngredient.value.qty = 0 // Corrected this line
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
            <!-- Modal with Form to Update Ingredient -->
            <EditIngredientModal
                :isOpen="isModalOpened"
                @modal-close="closeModal"
                @submit="submitHandler"
                name="first-modal"
            >
                <template #header>
                    <div>
                        Editing Quantity for: {{ selectedIngredient.name }}
                    </div>
                </template>
                <template #content>
                    <form @submit.prevent="submitHandler">
                        <div class="mb-4">
                            <label class="block text-gray-700"
                                >Quantity (หน่วย)</label
                            >
                            <input
                                v-model="selectedIngredient.qty"
                                min="0"
                                max="1000"
                                step="0.01"
                                @input="validateRange()"
                                type="number"
                                class="border p-2 w-full"
                                required
                            />
                        </div>
                    </form>
                </template>
                <template #footer>
                    <div class="flex justify-end gap-4">
                        <button
                            @click="closeModal"
                            class="bg-gray-300 px-4 py-2 rounded-md"
                        >
                            Cancel
                        </button>
                        <button
                            @click="submitHandler"
                            class="bg-blue-500 text-white px-4 py-2 rounded-md"
                        >
                            Save
                        </button>
                    </div>
                </template>
            </EditIngredientModal>

            <section class="flex gap-4">
                <Search @update-search="searchQuery = $event" />
                <div
                    style="
                        display: flex;
                        background-color: #e5e7eb;
                        align-items: center;
                        border-radius: 0.5rem;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        padding: 0.5rem;
                        transition: transform 0.3s;
                    "
                    onmouseover="this.style.transform='scale(1.05)'"
                    onmouseout="this.style.transform='scale(1)'"
                >
                    <span
                        v-if="role === `ADMIN`"
                        style="
                            display: flex;
                            align-items: center;
                            cursor: pointer;
                        "
                        @click="addingredients()"
                    >
                        <fa icon="add" style="margin-right: 0.25rem" />
                        Ingredient
                    </span>
                </div>
            </section>

            <div class="text-2xl font-bold">Ingredients</div>

            <section class="pb-12">
                <ul class="ingredients-grid">
                    <li
                        v-for="ingredient in filteredIngredients"
                        :key="ingredient.id"
                    >
                        <IngredientCard
                            class="animate-slideUp"
                            @click="openModal(ingredient)"
                            :ingredientData="ingredient"
                        />
                    </li>
                </ul>
            </section>
        </main>
    </div>
</template>
