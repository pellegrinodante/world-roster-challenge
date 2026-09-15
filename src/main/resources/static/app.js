const positions = ["TOP", "JUNGLE", "MID", "ADC", "SUPPORT"];
const positionNames = { TOP:"Top", JUNGLE:"Jungla", MID:"Mid", ADC:"ADC", SUPPORT:"Support" };
let gameId;
let currentRound = 0;
const selected = {};

const roster = document.getElementById("roster");
const options = document.getElementById("options");
const roundText = document.getElementById("roundText");
const statusText = document.getElementById("statusText");
const spinButton = document.getElementById("spinButton");
const simulation = document.getElementById("simulation");
const simulateButton = document.getElementById("simulateButton");
const result = document.getElementById("result");

async function request(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "Ocurrió un error inesperado");
    return data;
}

async function startGame() {
    const data = await request("/api/games", { method:"POST" });
    gameId = data.gameId;
    currentRound = 0;
    Object.keys(selected).forEach(key => delete selected[key]);
    renderRoster();
    updateRound();
}

function renderRoster() {
    roster.innerHTML = positions.map(position => {
        const player = selected[position];
        return `<article class="roster-card"><h3>${positionNames[position]}</h3><p>${player ? `${player.nickname}<br>${player.team} · ${player.year}` : "Sin seleccionar"}</p></article>`;
    }).join("");
}

function updateRound() {
    if (currentRound === positions.length) {
        roundText.textContent = "Roster completo";
        statusText.textContent = "Ya puedes simular el torneo.";
        spinButton.disabled = true;
        simulation.classList.remove("hidden");
        return;
    }
    roundText.textContent = `Ronda ${currentRound + 1} de ${positions.length}: ${positionNames[positions[currentRound]]}`;
    statusText.textContent = "Obtén tres candidatos y elige uno.";
}

async function spin() {
    spinButton.disabled = true;
    try {
        const data = await request(`/api/games/${gameId}/draft/${positions[currentRound]}`);
        renderOptions(data.players);
    } catch (error) {
        options.innerHTML = `<p class="error">${error.message}</p>`;
    } finally {
        spinButton.disabled = false;
    }
}

function rating(player) {
    return (player.mechanics * .35 + player.consistency * .25 + player.experience * .2 + player.teamwork * .2).toFixed(1);
}

function renderOptions(players) {
    options.innerHTML = players.map(player => `
        <article class="player-card">
            <h2>${player.nickname}</h2>
            <p>${player.team} · ${player.region} · ${player.year}</p>
            <p>${player.achievement}</p>
            <p class="rating">Rating ${rating(player)}</p>
            <p>Mecánicas ${player.mechanics} · Consistencia ${player.consistency}</p>
            <button data-player="${player.id}">Seleccionar</button>
        </article>`).join("");
    document.querySelectorAll("[data-player]").forEach(button => {
        button.addEventListener("click", () => selectPlayer(players.find(p => p.id === Number(button.dataset.player))));
    });
}

async function selectPlayer(player) {
    try {
        await request(`/api/games/${gameId}/players/${player.id}`, { method:"POST" });
        selected[player.position] = player;
        currentRound++;
        options.innerHTML = "";
        renderRoster();
        updateRound();
    } catch (error) {
        alert(error.message);
    }
}

async function simulateTournament() {
    simulateButton.disabled = true;
    result.innerHTML = "<p>Simulando los cuatro grupos y el bracket...</p>";
    try {
        const seedValue = document.getElementById("seedInput").value.trim();
        const suffix = seedValue ? `?seed=${encodeURIComponent(seedValue)}` : "";
        renderSimulation(await request(`/api/games/${gameId}/simulate${suffix}`, { method:"POST" }));
    } catch (error) {
        result.innerHTML = `<p class="error">${error.message}</p>`;
    } finally {
        simulateButton.disabled = false;
    }
}

function renderSimulation(data) {
    const groupsHtml = data.groups.map(group => {
        const rows = group.standings.map(row => `
            <tr class="${row.position <= 2 ? "qualified" : "eliminated"} ${row.teamName === "Tu equipo" ? "user-row" : ""}">
                <td>${row.position}</td><td>${row.teamName}</td><td>${row.wins}</td><td>${row.losses}</td>
                <td>${row.performanceDifference > 0 ? "+" : ""}${row.performanceDifference}</td>
            </tr>`).join("");
        return `<section><h3>${group.groupName}</h3>
            <table><thead><tr><th>Pos.</th><th>Equipo</th><th>V</th><th>D</th><th>Dif.</th></tr></thead><tbody>${rows}</tbody></table>
            <details><summary>Ver partidas</summary><ol>${group.matchHistory.map(m => `<li>${m}</li>`).join("")}</ol></details></section>`;
    }).join("");

    const bracketHtml = data.bracket.map(series => `<article class="series-card">
        <strong>${series.stage}</strong>
        <p>${series.teamOne} ${series.teamOneWins} - ${series.teamTwoWins} ${series.teamTwo}</p>
        <small>Ganador: ${series.winner}</small>
        <details><summary>Ver partidas</summary><ol>${series.games.map(g => `<li>${g}</li>`).join("")}</ol></details>
    </article>`).join("");

    result.innerHTML = `<article class="result-card">
        <h2>${data.champion ? "🏆 ¡Campeón del mundo!" : `Resultado: ${data.finalStage}`}</h2>
        <p>Semilla: <strong>${data.seed}</strong>. Reutilízala para repetir exactamente el torneo.</p>
        <p>Tu grupo: <strong>${data.userGroupName}</strong>, puesto ${data.userGroupPosition}. Rating: ${data.teamRating.toFixed(1)}</p>
        <h2>Fase de grupos</h2><div class="groups-grid">${groupsHtml}</div>
        <h2>Fase eliminatoria</h2><div class="bracket-grid">${bracketHtml}</div>
        <h3>Campeón: ${data.worldChampion}</h3>
        <button id="restartButton">Nueva partida</button>
    </article>`;
    document.getElementById("restartButton").addEventListener("click", () => location.reload());
}

spinButton.addEventListener("click", spin);
simulateButton.addEventListener("click", simulateTournament);
startGame().catch(error => statusText.textContent = error.message);
