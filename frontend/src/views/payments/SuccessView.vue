<script setup>
import orderApi from '@/api/orderApi.js'
import Sidebar from '@/components/Sidebar.vue'
import router from '@/router/index.js'
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

onMounted(async () => {
    const orderId = route.query.id
    if (orderId) {
        try {
            // const { data: resQty } = await orderApi.updateOrderIngredientQty(
            //     orderId
            // )
            const { data: resStatus } = await orderApi.updateOrderStatus({
                id: orderId,
                status: 'COMPLETE',
            })

            // delete carts after paid
            localStorage.removeItem('carts')

            console.log('Order status updated:', resStatus.data)
        } catch (error) {
            console.error(
                'Failed to update order status:',
                error.response ? error.response.data : error.message
            )
        }
    } else {
        console.error('Order ID not found in the URL')
    }
})

const goToReceipt = () => {
    router.push(`/receipt/${route.query.id}`)
}
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-8 px-8 flex flex-col gap-6 bg-gray-100 h-screen items-center justify-center"
        >
            <div
                class="flex flex-col items-center bg-white p-8 rounded-lg shadow-lg gap-6"
            >
                <h1 class="text-green-500 text-4xl font-bold">
                    Payment Successful
                </h1>
                <div>
                    <img
                        src="../../assets/success_icon.png"
                        alt="Success Icon"
                        class="w-20 h-20"
                    />
                </div>
                <p class="text-lg text-gray-700 text-center">
                    Thank you for your payment! Your order has been completed
                    successfully.
                </p>
                <button
                    @click="goToReceipt()"
                    class="mt-4 bg-green-500 text-white py-2 px-4 rounded-lg shadow hover:bg-green-600 transition-all duration-200"
                >
                    Check your receipt!
                </button>
            </div>
        </main>
    </div>
</template>
