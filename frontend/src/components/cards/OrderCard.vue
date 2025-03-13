<template>
    <div
        class="flex justify-between px-3 py-2 shadow-md rounded-xl bg-white mb-5"
    >
        <div class="flex flex-col">
            <div class="flex items-center">
                <span class="text-3xl pr-2"
                    >Order #{{ order.id.slice(0, 4) }}</span
                >
                <span
                    class="align-text-bottom text-lg"
                    :style="{
                        color:
                            order.status === 'COMPLETE'
                                ? '#ADE92E'
                                : order.status === 'COOKING'
                                ? '#FF6B00'
                                : order.status === 'PENDING'
                                ? '#FF6B00'
                                : order.status === 'SUCCESS'
                                ? '#000000'
                                : '#FF5151',
                    }"
                >
                    ●
                </span>
                <span class="align-text-bottom text-lg ml-1">{{
                    order.status === 'COMPLETE' ? 'PAID' : order.status
                }}</span>
            </div>
            <span class="text-md text-gray-500 py-1 ml-1"
                >By {{ order.user.username }}</span
            >
            <span class="text-md text-gray-500 ml-1"
                >Date: {{ order.createdAt.slice(0, 10) }}</span
            >
            <span class="text-md text-gray-500 ml-1"
                >Time: {{ order.createdAt.slice(11, 19) }}</span
            >
            <span class="py-2 pl-1">
                <button
                    v-if="order.status === 'DELIVERED' && role === 'ADMIN'"
                    class="inline-block w-52 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderSuccess(order.id)"
                >
                    Mark as Success
                </button>
                <button
                    v-if="order.status === 'COMPLETE' && role === 'COOK'"
                    class="inline-block w-70 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderCooking(order.id)"
                >
                    Cooking
                </button>
                <button
                    v-if="order.status === 'COMPLETE' && role === 'COOK'"
                    class="inline-block w-70 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #dddddd; color: #000000"
                    @click="viewOrderDetailforCook(order.id)"
                >
                    View Details
                </button>
                <button
                    v-if="order.status === 'COOKING' && role === 'COOK'"
                    class="inline-block w-70 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderReady(order.id)"
                >
                    Mark as Ready
                </button>
                <button
                    v-if="order.status === 'READY' && role === 'RIDER'"
                    class="inline-block w-70 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderDelivering(order.id)"
                >
                    Mark as Delivering
                </button>
                <button
                    v-if="order.status === 'DELIVERING' && role === 'RIDER'"
                    class="inline-block w-56 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderDelivered(order.id)"
                >
                    Mark as Delivered
                </button>
                <button
                    v-if="order.status === 'PENDING'"
                    class="inline-block w-52 px-10 py-2 mt-2 mr-10 rounded-lg bg-yellow-300"
                    @click="payAgain(order)"
                >
                    Pay Again
                </button>
                <botton
                    v-if="order.status === 'SUCCESS'"
                    class="text-center inline-block w-52 px-10 py-2 mt-2 mr-10 rounded-lg bg-yellow-300 cursor-pointer"
                    style="background-color: #ff7f50; color: #ffffff"
                    @click="reviewOrder(order.id)"
                >
                    Review Order
                </botton>
            </span>
        </div>
        <div class="flex flex-col items-end">
            <span class="text-4xl text-gray-750"
                >{{ (order.total + order.total * 0.07).toFixed(2) }} ฿</span
            >
        </div>
    </div>
</template>

<script setup>
import orderApi from '@/api/orderApi'
import userApi from '@/api/userApi'
import router from '@/router'
import { onMounted, ref } from 'vue'

const role = ref('')

onMounted(async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role
})

const props = defineProps({
    order: {
        type: Object,
        required: true,
    },
    index: Number,
})

const emit = defineEmits([
    'mark-success',
    'mark-cooking',
    'mark-ready',
    'view-detail',
    'show-recipe',
    'mark-delivering',
    'mark-delivered',
])

const markOrderSuccess = async (id) => {
    try {
        await orderApi.updateOrderStatus({ id, status: 'SUCCESS' })
        emit('mark-success', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as success:', error)
    }
}

const markOrderCooking = async (id) => {
    try {
        const { data: resQty } = await orderApi.updateOrderIngredientQty(id)
        await orderApi.updateOrderStatus({ id, status: 'COOKING' })
        console.log(resQty)
        emit('mark-cooking', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as cooking:', error)
    }
}

const markOrderDelivering = async (id) => {
    try {
        await orderApi.updateOrderStatus({ id, status: 'DELIVERING' })
        emit('mark-delivering', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as delivering:', error)
    }
}

const markOrderDelivered = async (id) => {
    try {
        await orderApi.updateOrderStatus({ id, status: 'DELIVERED' })
        emit('mark-delivered', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as delivered:', error)
    }
}

const markOrderReady = async (id) => {
    try {
        await orderApi.updateOrderStatus({ id, status: 'READY' })
        emit('mark-ready', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as ready:', error)
    }
}

const viewOrderDetail = () => {
    emit('view-detail', props.order.id)
}

const viewOrderDetailforCook = () => {
    router.push({ name: 'receiptforcook', params: { id: props.order.id } })
}

const payAgain = (order) => {
    window.location.href = order.paymentLink
    // console.log('Redirecting to payment link:', order.paymentLink)
}

const reviewOrder = (id) => {
    console.log('Reviewing order:', id)
    window.location.href = `/order/${id}/review`
}
</script>
