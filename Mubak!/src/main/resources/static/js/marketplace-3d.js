// Vitrine 3D da home: cria a cena Three.js, anima os produtos e reage ao mouse.
(function () {
    let scene, camera, renderer, raycaster;
    let centralProduct, productRing = [];
    let priceTags = [];
    let autoRotAngle = 0;
    let targetFocus = null;
    let clock;
    let heroVisual = null;
    let resizeObserver = null;
    let cameraTarget = null;
    const pointer = { x: 0, y: 0 };

    const COLORS = {
        brand: 0x3a86ff,
        stage: 0xe0eaf2,
        product: 0x1a3652,
        productAccent: 0x4a6fa5,
        card: 0xffffff
    };

    const STAGE_RADIUS = 2;
    const RING_RADIUS = 1.7;
    const CAMERA_ELEVATION = 0.3;
    const FIT_HALF_WIDTH = 2.3;
    const FIT_HALF_HEIGHT = 1.5;

    let ambientLight, dirLight, brandLight;

    function init() {
        // A inicialização é tolerante: sem Three.js ou canvas, a home usa o fallback HTML.
        if (scene) return;
        const canvas = document.getElementById('marketplace-canvas');
        if (typeof THREE === 'undefined') return;
        if (!canvas) {
            onThreeUnavailable();
            return;
        }

        clock = new THREE.Clock();
        scene = new THREE.Scene();
        scene.background = new THREE.Color(0xe8edf2);
        scene.fog = new THREE.FogExp2(0xe8edf2, 0.04);

        heroVisual = canvas.closest('.hero-visual');

        const size = measureCanvas(canvas);
        const w = size.width;
        const h = size.height;

        camera = new THREE.PerspectiveCamera(35, w / h, 0.1, 100);
        cameraTarget = new THREE.Vector3(0, -0.15, 0);
        frameCamera(w, h);

        renderer = new THREE.WebGLRenderer({
            canvas: canvas,
            alpha: true,
            antialias: true,
            powerPreference: 'high-performance'
        });
        renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 1.5));
        renderer.setSize(w, h, false);
        renderer.shadowMap.enabled = true;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        renderer.toneMapping = THREE.ACESFilmicToneMapping;
        renderer.toneMappingExposure = 1.05;
        applyRendererColorSpace(renderer);

        raycaster = new THREE.Raycaster();

        const hemi = new THREE.HemisphereLight(0xffffff, 0x9fb3c8, 0.9);
        scene.add(hemi);

        dirLight = new THREE.DirectionalLight(0xffffff, 1.2);
        dirLight.position.set(4, 10, 6);
        dirLight.castShadow = true;
        dirLight.shadow.camera.left = -6;
        dirLight.shadow.camera.right = 6;
        dirLight.shadow.camera.top = 8;
        dirLight.shadow.camera.bottom = -8;
        dirLight.shadow.mapSize.set(1024, 1024);
        dirLight.shadow.bias = -0.0005;
        scene.add(dirLight);

        ambientLight = new THREE.AmbientLight(0x9fb3c8, 0.5);
        scene.add(ambientLight);

        brandLight = new THREE.PointLight(COLORS.brand, 0, 3);
        brandLight.position.set(0, 1.5, 1.5);
        scene.add(brandLight);

        createStage();
        createCentralProduct();
        createProductRing();
        createPriceTags();

        canvas.addEventListener('mousemove', onPointerMove);
        canvas.addEventListener('mouseenter', function () {
            canvas.dataset.hovered = 'true';
        });
        canvas.addEventListener('mouseleave', function () {
            canvas.dataset.hovered = 'false';
            targetFocus = null;
            brandLight.intensity = 0;
        });

        canvas.style.cursor = 'grab';
        document.body.style.cursor = '';

        window.addEventListener('resize', onResize);
        animate();
    }

    function measureCanvas(canvas) {
        const rect = canvas.getBoundingClientRect();
        const host = canvas.parentElement ? canvas.parentElement.getBoundingClientRect() : rect;
        const width = Math.round(rect.width) || Math.round(host.width) || canvas.clientWidth || 560;
        const height = Math.round(rect.height) || Math.round(host.height) || canvas.clientHeight || 540;
        return {
            width: Math.max(width, 260),
            height: Math.max(height, 320)
        };
    }

    function applyRendererColorSpace(target) {
        if ('outputColorSpace' in target && THREE.SRGBColorSpace) {
            target.outputColorSpace = THREE.SRGBColorSpace;
        } else if (THREE.sRGBEncoding !== undefined) {
            target.outputEncoding = THREE.sRGBEncoding;
        }
    }

    function applyTextureColorSpace(texture) {
        if ('colorSpace' in texture && THREE.SRGBColorSpace) {
            texture.colorSpace = THREE.SRGBColorSpace;
        } else if (THREE.sRGBEncoding !== undefined) {
            texture.encoding = THREE.sRGBEncoding;
        }
        texture.minFilter = THREE.LinearFilter;
        texture.magFilter = THREE.LinearFilter;
        return texture;
    }

    function createStage() {
        // Palco circular e linha de contorno que sustentam visualmente os produtos.
        const stageMat = new THREE.MeshStandardMaterial({
            color: COLORS.stage,
            metalness: 0.05,
            roughness: 0.5,
            transparent: true,
            opacity: 0.6
        });
        const stageGeo = new THREE.CylinderGeometry(STAGE_RADIUS, STAGE_RADIUS, 0.25, 48);
        const stage = new THREE.Mesh(stageGeo, stageMat);
        stage.position.y = -1.2;
        stage.receiveShadow = true;
        scene.add(stage);

        const edgePoints = [];
        const edgeSegments = 96;
        for (let i = 0; i < edgeSegments; i++) {
            const a = (i / edgeSegments) * Math.PI * 2;
            edgePoints.push(new THREE.Vector3(Math.cos(a) * (STAGE_RADIUS + 0.06), -1.1, Math.sin(a) * (STAGE_RADIUS + 0.06)));
        }
        const edgeMat = new THREE.LineBasicMaterial({
            color: 0xa8bfd2,
            transparent: true,
            opacity: 0.55
        });
        const edgeGeo = new THREE.BufferGeometry().setFromPoints(edgePoints);
        const edge = new THREE.LineLoop(edgeGeo, edgeMat);
        scene.add(edge);
    }

    function createCentralProduct() {
        const group = new THREE.Group();

        const geo = new THREE.BoxGeometry(1, 1.4, 1);

        const box = new THREE.Mesh(geo, createBoxMaterials('#1a3652', '#ffffff', 'MUBAK', 0.35, 0.55));
        box.castShadow = true;
        box.receiveShadow = true;
        group.add(box);

        const glowMat = new THREE.MeshBasicMaterial({
            color: COLORS.brand,
            transparent: true,
            opacity: 0.15,
            depthWrite: false,
            side: THREE.BackSide
        });
        const glowGeo = new THREE.BoxGeometry(1.2, 1.6, 1.2);
        const glow = new THREE.Mesh(glowGeo, glowMat);
        group.add(glow);

        group.position.set(0, -0.3, 0);
        scene.add(group);
        centralProduct = group;
    }

    function createProductRing() {
        // Produtos decorativos distribuídos em anel ao redor do item central.
        const ringRadius = RING_RADIUS;
        const count = 6;

        for (let i = 0; i < count; i++) {
            const angle = (i / count) * Math.PI * 2;
            const x = Math.cos(angle) * ringRadius;
            const z = Math.sin(angle) * ringRadius;

            const group = new THREE.Group();

            const isVertical = i % 2 === 0;
            const w = isVertical ? 0.8 : 1;
            const h = isVertical ? 1.15 : 0.8;
            const geo = new THREE.BoxGeometry(w, h, 0.22);

            const color = i % 2 === 0 ? COLORS.productAccent : COLORS.card;
            const label = i % 2 === 0 ? '#ffffff' : '#1a3652';
            const labelText = ['M', 'K', 'B', 'A', 'K', '★'][i];

            const box = new THREE.Mesh(geo, createBoxMaterials(color, label, labelText, 0.25, 0.5));
            box.castShadow = true;
            box.receiveShadow = true;
            group.add(box);

            const priceTag = create3DPriceTag(formatPrice((i + 1) * 29.9));
            priceTag.position.y = h / 2 + 0.12;
            group.add(priceTag);

            group.position.set(x, 0.4, z);
            group.userData.angle = angle;
            group.userData.baseY = 0.4;
            group.userData.tagOffset = h / 2 + 0.12;
            group.userData.index = i;
            group.userData.isRingProduct = true;

            scene.add(group);
            productRing.push(group);
        }
    }

    function createPriceTags() {
        const tags = [
            { text: 'R$ 89,90', x: -1.25, y: -0.7, z: 2.25 },
            { text: 'R$ 129,00', x: 1.35, y: -0.6, z: 2.15 }
        ];

        tags.forEach(function (info) {
            const tag = create3DPriceTag(info.text);
            tag.position.set(info.x, info.y, info.z);
            tag.userData.baseY = info.y;
            scene.add(tag);
            priceTags.push(tag);
        });
    }

    function formatPrice(value) {
        return 'R$ ' + value.toFixed(2).replace('.', ',');
    }

    function create3DPriceTag(text) {
        const canvas = document.createElement('canvas');
        canvas.width = 256;
        canvas.height = 80;
        const ctx = canvas.getContext('2d');
        const label = String(text);

        ctx.fillStyle = 'rgba(0,0,0,0.75)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        let fontSize = 40;
        ctx.font = 'bold ' + fontSize + 'px Arial, sans-serif';
        while (ctx.measureText(label).width > canvas.width - 28 && fontSize > 14) {
            fontSize -= 2;
            ctx.font = 'bold ' + fontSize + 'px Arial, sans-serif';
        }

        ctx.fillStyle = '#ffffff';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(label, canvas.width / 2, canvas.height / 2 + 2);

        const texture = applyTextureColorSpace(new THREE.CanvasTexture(canvas));

        const mat = new THREE.MeshBasicMaterial({
            map: texture,
            transparent: true,
            depthWrite: false,
            side: THREE.DoubleSide
        });
        const geo = new THREE.PlaneGeometry(0.45, 0.14);
        const mesh = new THREE.Mesh(geo, mat);
        mesh.userData.isPriceTag = true;
        return mesh;
    }

    function createBoxMaterials(bgColor, textColor, labelText, metalness, roughness) {
        const faceMaterial = new THREE.MeshStandardMaterial({
            map: createProductTexture(bgColor, textColor, labelText),
            metalness: metalness,
            roughness: roughness
        });
        const capMaterial = new THREE.MeshStandardMaterial({
            map: createProductTexture(bgColor, textColor, ''),
            metalness: metalness,
            roughness: roughness
        });
        return [faceMaterial, faceMaterial, capMaterial, capMaterial, faceMaterial, faceMaterial];
    }

    function createProductTexture(bgColor, textColor, labelText) {
        const canvas = document.createElement('canvas');
        canvas.width = 256;
        canvas.height = 256;
        const ctx = canvas.getContext('2d');

        const gradient = ctx.createLinearGradient(0, 0, 0, 256);
        gradient.addColorStop(0, shadeColor(bgColor, 15));
        gradient.addColorStop(1, shadeColor(bgColor, -20));
        ctx.fillStyle = gradient;
        ctx.fillRect(0, 0, 256, 256);

        const label = String(labelText);
        ctx.fillStyle = textColor;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';

        let fontSize = 120;
        ctx.font = 'bold ' + fontSize + 'px Arial, sans-serif';
        while (ctx.measureText(label).width > canvas.width - 36 && fontSize > 40) {
            fontSize -= 4;
            ctx.font = 'bold ' + fontSize + 'px Arial, sans-serif';
        }
        ctx.fillText(label, canvas.width / 2, canvas.height / 2 + fontSize * 0.06);

        return applyTextureColorSpace(new THREE.CanvasTexture(canvas));
    }

    function shadeColor(color, percent) {
        let r, g, b;
        if (typeof color === 'number') {
            r = (color >> 16) & 0xff;
            g = (color >> 8) & 0xff;
            b = color & 0xff;
        } else {
            let hex = color.replace('#', '');
            r = parseInt(hex.substr(0, 2), 16);
            g = parseInt(hex.substr(2, 2), 16);
            b = parseInt(hex.substr(4, 2), 16);
        }
        r = Math.max(0, Math.min(255, r + percent));
        g = Math.max(0, Math.min(255, g + percent));
        b = Math.max(0, Math.min(255, b + percent));
        return 'rgb(' + r + ',' + g + ',' + b + ')';
    }

    function onPointerMove(e) {
        // Raycaster identifica o produto sob o cursor e acende a luz de destaque.
        const rect = renderer.domElement.getBoundingClientRect();
        pointer.x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
        pointer.y = -((e.clientY - rect.top) / rect.height) * 2 + 1;

        raycaster.setFromCamera(pointer, camera);
        const intersects = raycaster.intersectObjects(
            productRing.map(function (g) {
                return g.children[0];
            })
        );

        if (intersects.length > 0) {
            const obj = intersects[0].object;
            const group = obj.parent;
            if (group.userData.isRingProduct) {
                targetFocus = group;
                brandLight.intensity = 0.8;
                renderer.domElement.style.cursor = 'pointer';
            }
        } else {
            targetFocus = null;
            brandLight.intensity = 0.3;
            renderer.domElement.style.cursor = 'grab';
        }
    }

    function frameCamera(width, height) {
        if (!camera || !cameraTarget) return;
        const aspect = Math.max(width, 1) / Math.max(height, 1);
        const verticalHalf = THREE.MathUtils.degToRad(camera.fov) / 2;
        const horizontalHalf = Math.atan(Math.tan(verticalHalf) * aspect);
        const fitWidth = FIT_HALF_WIDTH / Math.tan(horizontalHalf);
        const fitHeight = FIT_HALF_HEIGHT / Math.tan(verticalHalf);
        const distance = Math.max(fitWidth, fitHeight);

        camera.aspect = aspect;
        camera.position.set(
            cameraTarget.x,
            cameraTarget.y + Math.sin(CAMERA_ELEVATION) * distance,
            cameraTarget.z + Math.cos(CAMERA_ELEVATION) * distance
        );
        camera.lookAt(cameraTarget);
        camera.updateProjectionMatrix();
    }

    function onResize() {
        if (!camera || !renderer) return;
        const canvas = renderer.domElement;
        const width = canvas.clientWidth;
        const height = canvas.clientHeight;
        if (width <= 0 || height <= 0) return;
        if (canvas.width !== width || canvas.height !== height) {
            renderer.setSize(width, height, false);
        }
        frameCamera(width, height);
    }

    function animate() {
        requestAnimationFrame(animate);
        const delta = Math.min(clock.getDelta(), 0.05);
        const t = clock.elapsedTime;

        const isHovered = heroVisual ? heroVisual.matches(':hover') : false;

        if (!targetFocus && !isHovered) {
            autoRotAngle += delta * 0.25;
        }

        if (centralProduct) {
            centralProduct.rotation.y += delta * 0.3 + Math.sin(t * 0.5) * 0.001;
        }

        productRing.forEach(function (group, i) {
            if (targetFocus === group) {
                const targetAngle = group.userData.angle + Math.PI;
                let diff = targetAngle - (group.rotation.y % (Math.PI * 2));
                while (diff > Math.PI) diff -= Math.PI * 2;
                while (diff < -Math.PI) diff += Math.PI * 2;
                group.rotation.y += diff * 0.12;
            } else if (isHovered && !targetFocus) {
                group.rotation.y += ((i / productRing.length) * Math.PI * 2 + autoRotAngle - group.rotation.y) * 0.1;
            }

            const floatY = Math.sin(t * 1.2 + i * 0.8) * 0.08;
            group.position.y = group.userData.baseY + floatY;

            const tag = group.children[1];
            if (tag && tag.userData.isPriceTag) {
                tag.rotation.y = -group.rotation.y;
                tag.position.y = group.userData.tagOffset + floatY;
            }
        });

        priceTags.forEach(function (tag, i) {
            tag.rotation.y = -autoRotAngle * 0.3 + i * 0.1;
            tag.position.y += Math.sin(t * 1.5 + i * 2) * 0.01 - (tag.position.y - tag.userData.baseY) * 0.1;
        });

        brandLight.position.x = pointer.x * 0.75;
        brandLight.position.y = Math.max(0.8, 1.5 + pointer.y * 0.3);

        renderer.render(scene, camera);
    }

    function onThreeReady(retries) {
        retries = retries || 0;
        if (typeof THREE !== 'undefined') {
            const host = document.querySelector('.hero-visual');
            if (host) {
                host.classList.add('threejs-loaded');
            }
            try {
                init();
                watchLayout();
                requestAnimationFrame(onResize);
            } catch (e) {
                console.error('3D init error:', e);
                onThreeUnavailable();
            }
        } else if (retries < 20) {
            setTimeout(function () { onThreeReady(retries + 1); }, 100);
        } else {
            onThreeUnavailable();
        }
    }

    function onThreeUnavailable() {
        const host = document.querySelector('.hero-visual');
        if (host) {
            host.classList.remove('threejs-loaded');
            host.classList.add('threejs-failed');
        }
    }

    function watchLayout() {
        if (typeof ResizeObserver === 'undefined' || !heroVisual) return;
        resizeObserver = new ResizeObserver(function () { onResize(); });
        resizeObserver.observe(heroVisual);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', onThreeReady);
    } else {
        onThreeReady();
    }
})();
