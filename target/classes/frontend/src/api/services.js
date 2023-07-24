import axios from "axios";

export async function splitImage(imageUrl) {
    const response = await axios.post("http://localhost:8081/api/split", {
        imageUrl: imageUrl,
    });
    return response.data;
}

export async function verifyPuzzles(puzzles) {
    const response = await axios.post("http://localhost:8081/api/verifyPuzzles", puzzles);
    return response.data;
}