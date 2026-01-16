/**
 * Blockbench Script: Flag Wave Animation Generator
 *
 * HOW TO USE:
 * 1. First run blockbench_flagpole.js to create the model
 * 2. Press Ctrl+Shift+I to open DevTools > Console
 * 3. Paste this script and press Enter
 * 4. Click "Animate" tab in top toolbar to see the animation
 * 5. Press Space to play/pause
 */

(function() {
    // Find the flag bone
    const flagBone = Group.all.find(g => g.name === 'flag');

    if (!flagBone) {
        Blockbench.showQuickMessage('Error: No "flag" bone found!', 3000);
        return;
    }

    // Remove existing animation if present
    const existing = Animation.all.find(a => a.name === 'flag_wave');
    if (existing) {
        existing.remove();
    }

    // Create the animation
    const anim = new Animation({
        name: 'flag_wave',
        loop: 'loop',
        length: 2
    });
    anim.add();

    // Select animation first (required for keyframe creation)
    anim.select();

    // Create bone animator for the flag
    const boneAnim = anim.getBoneAnimator(flagBone);

    // Add keyframes - gentle Z rotation (side-to-side wave)
    const keyframeData = [
        { time: 0,    z: 0 },
        { time: 0.5,  z: 10 },
        { time: 1,    z: 0 },
        { time: 1.5,  z: -10 },
        { time: 2,    z: 0 }
    ];

    // Add keyframes using Blockbench's undo system
    Undo.initEdit({keyframes: []});

    keyframeData.forEach(kf => {
        const keyframe = new Keyframe({
            time: kf.time,
            channel: 'rotation',
            data_points: [{x: 0, y: 0, z: kf.z}]
        }, null, boneAnim);
        keyframe.addTo(boneAnim);
    });

    Undo.finishEdit('Add flag wave keyframes');

    // Reset timeline to start
    if (typeof Timeline !== 'undefined') {
        Timeline.time = 0;
    }

    Canvas.updateAll();

    Blockbench.showQuickMessage('Animation created! Click "Animate" tab, then press Space to play.', 3000);

    console.log(`
========================================
Flag Wave Animation Created!
========================================

To preview:
1. Click "Animate" tab in top toolbar
2. Select "flag_wave" animation (left panel)
3. Press Space to play/pause

The flag will wave gently side-to-side.
========================================
`);

})();
