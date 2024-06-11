import axios from 'axios';

interface Station {
    id: number;
    number: string;
    description: string;
    latitude: number;
    longitude: number;
    altitude: number;
    type: string;
    vehicles: Vehicle[];
}

interface Vehicle {
    number: string;
    status: string;
}

const API_URL = 'http://localhost:8084/vehicle-station-service/station/';

// Функція для отримання станцій, що підтримує фільтрацію за типом "BASING"
const fetchStations = async (token: string): Promise<{ label: string; value: string }[]> => {
    const response = await axios.get<Station[]>(`${API_URL}get-all`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    // Фільтрація станцій за типом "BASING" і адаптація для дропдауна
    return response.data
        .filter(station => station.type === "BASING")
        .map(station => ({
            label: station.number,
            value: station.number
        }));
}

export { fetchStations };