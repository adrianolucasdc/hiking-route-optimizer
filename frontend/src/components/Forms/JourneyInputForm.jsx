import React, { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../../services/api';

export default function JourneyInputForm({ count, onConfirm, onBack, formattedMatrix }) {
    const [startPoint, setStartPoint] = useState('');
    const [endPoint, setEndPoint] = useState('');

    const getPointName = (index) => String.fromCharCode(65 + index);

    const handleConfirm = async () => {
        if (!startPoint || !endPoint) {
            toast.error("Selecione os dois pontos para confirmar a jornada!");
            return;
        }

        // Converte o formattedMatrix para matriz numérica 2D
        const numericMatrix = formattedMatrix.map(row =>
            row.dados.map(cell => (cell.checked ? Number(cell.value) : 0))
        );

        try {
            const response = await api.post("/api/grafo/processar", {
                origem: startPoint,
                destino: endPoint,
                matriz: numericMatrix
            });

            console.log("Resposta do backend:", response.data);
            toast.success("Jornada enviada com sucesso!");

            if (onConfirm) {
                onConfirm({ startPoint, endPoint });
            }
        } catch (error) {
            console.error("Erro ao enviar jornada:", error);
            toast.error("Erro ao enviar jornada.");
        }
    };

    const pointOptions = Array.from({ length: count }, (_, i) => getPointName(i));

    return (
        <div className="p-4 md:p-8 w-full max-w-lg mx-auto">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold font-mono">Defina sua Jornada</h2>
                <button
                    onClick={onBack}
                    className="bg-gray-500 text-white font-mono py-2 px-4 rounded-lg hover:bg-gray-600 text-sm"
                >
                    Voltar
                </button>
            </div>

            <div className="flex flex-col items-center justify-center my-4 gap-4">
                <div className="flex flex-col items-center gap-2">
                    <label htmlFor="startPoint" className="font-mono font-bold">
                        Ponto de Partida:
                    </label>
                    <select
                        id="startPoint"
                        className="w-80 h-10 border-2 border-black bg-white rounded-lg px-2 text-center font-mono"
                        value={startPoint}
                        onChange={(e) => setStartPoint(e.target.value)}
                    >
                        <option value="">Selecione o Ponto de Partida</option>
                        {pointOptions.map(point => (
                            <option key={point} value={point}>{point}</option>
                        ))}
                    </select>
                </div>

                <div className="flex flex-col items-center gap-2">
                    <label htmlFor="endPoint" className="font-mono font-bold">
                        Ponto de Chegada:
                    </label>
                    <select
                        id="endPoint"
                        className="w-80 h-10 border-2 border-black bg-white rounded-lg px-2 text-center font-mono"
                        value={endPoint}
                        onChange={(e) => setEndPoint(e.target.value)}
                    >
                        <option value="">Selecione o Ponto de Chegada</option>
                        {pointOptions.map(point => (
                            <option key={point} value={point}>{point}</option>
                        ))}
                    </select>
                </div>

                <button
                    onClick={handleConfirm}
                    className="bg-black text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-gray-800 mt-4"
                >
                    Confirmar Jornada
                </button>
            </div>
        </div>
    );
}
