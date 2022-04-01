import axios from 'axios'

function distanceN(setA, setB){
    let dist = 0;
    for (let i = 0; i < setA.length; i++) {
        dist += Math.abs(setA[i] - setB[i])
    }
    return dist
}

function distanceC(setA, setB){
    let dist = 0;
    for (let i = 0; i < setA.length; i++) {
        let minSet = setB[i];
        let maxSet = setA[i];
        if (minSet.length < minSet.length) {
            minSet = setA[i]
            maxSet = setB[i]
        }
        let miss = 0
        for (let d of minSet) {
            if (maxSet.indexOf(d) < 0) {
                miss++
            }
        }
        dist += 1 - miss * 2 / (minSet.length + maxSet.length)
    }
    return 1 - dist / setA.length
}

let localAxios = axios.create({
    baseURL: '/python',
    timeout: 30000,
    headers: {
        'content-type': 'application/x-www-form-urlencoded'
    },
    withCredentials:true
});

function distributionN(setA, setB) {
    return localAxios.post("ks_test", {
        setA,setB
    })
}

function distributionC(setA, setB) {
    let set = new Set()
    let mapA = {}
    for (let data of setA) {
        for (let d of data) {
            set.add(d)
            if (!mapA[d]) {
                mapA[d] = 0
            }
            mapA[d] += 1 / data.length
        }
    }

    let mapB = {}
    for (let data of setB) {
        for (let d of data) {
            set.add(d)
            if (!mapB[d]) {
                mapB[d] = 0
            }
            mapB[d] += 1 / data.length
        }
    }

    let dist = 0;
    set.forEach(key=>{
        if (!mapA[key]) {
            mapA[key] = 0
        }
        if (!mapB[key]) {
            mapB[key] = 0
        }
        dist += Math.abs(mapA[key] - mapB[key])
    })
    return dist
}

function sigNum(num) {
    if (num < 0) return -1;
    if (num > 0) return 1;
    return 0;
}

function laplaceNoise(mu, lambda) {
    let r = Math.random() - 0.5;
    return mu - lambda * sigNum(r) * Math.log(1 - 2*Math.abs(r))
}

function laplaceCDF(mu, lambda, x) {
    if (x < mu) {
        return 0.5 * Math.exp((x-mu)/lambda)
    } else {
        return 1- 0.5 * Math.exp((mu-x)/lambda)
    }
}

export{distanceN, distanceC, distributionN, distributionC, laplaceNoise, laplaceCDF}