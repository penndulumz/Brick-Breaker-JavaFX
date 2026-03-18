# BrickBreakerFX

A JavaFX brick-breaker game built as an Object-Oriented Programming school project.

This project focuses on clean architecture, gameplay extensibility, and reusable game systems (levels, collisions, power-ups, enemies, effects, and leaderboard persistence).

## Project Goals

- Build a complete desktop arcade game with Java and JavaFX.
- Apply OOP principles in a practical, medium-sized codebase.
- Use a layered architecture so logic, rendering, and input handling stay decoupled.
- Keep gameplay systems modular for easy extension.

## Features

- Classic brick-breaker gameplay with lives, score, and levels.
- Multiple brick types:
  - Normal
  - Strong
  - Extra strong
  - Unbreakable
  - Colored variants
- Power-ups:
  - Expand paddle
  - Fast ball
  - Multi-ball
  - Extra life
  - Gun mode
- Enemy system with different behaviors.
- Visual effects:
  - Brick hit blink
  - Enemy destruction animation
  - Particle burst effects
- Audio feedback for key events.
- Persistent leaderboard stored in `leaderboard.txt`.

## Controls

- `A`: Move paddle left
- `D`: Move paddle right
- `P`: Pause/Resume
- `SPACE`: Start game from menu
- `L`: Open leaderboard in menu
- `ESC`: Return from leaderboard

## Tech Stack

- Java 24
- JavaFX (`controls`, `graphics`, `fxml`, `media`)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- JUnit 5

## Project Structure

```text
Brick-Breaker-JavaFX/
  class-diagram.puml
  leaderboard.txt
  pom.xml
  README.md
  src/
    main/
      java/
        org/OOPproject/BrickBreakerFX/
          BrickBreakerGame.java
          controller/
            EndGameController.java
            GameController.java
            MenuController.java
          model/
            Ball.java
            Bullet.java
            Enemy.java
            EnemyType.java
            GameEngine.java
            GameObject.java
            Level.java
            MovableObject.java
            MovementType.java
            Paddle.java
            Sprite.java
            Bricks/
              Brick.java
              BrickFactory.java
              BrickType.java
              ColoredBrick.java
              ExtraStrongBrick.java
              NormalBrick.java
              StrongBrick.java
              UnbreakableBrick.java
            PowerUps/
              ExpandPaddlePowerUp.java
              ExtraLifePowerUp.java
              FastBallPowerUp.java
              GunPowerUp.java
              MultiBallPowerUp.java
              PowerUp.java
              PowerUpTypes.java
            effects/
              Blink.java
              Destroy.java
              Particle.java
              ParticleSystem.java
            engine/
              CollisionHandler.java
              EnemySpawner.java
            managers/
              LeaderboardManager.java
              SoundManager.java
          utils/
            Constants.java
            GameState.java
            InputSignal.java
          view/
            AssetManager.java
            EndGameView.java
            GameView.java
            MenuView.java
      resources/
        assets/
          Emulogic-zrEw.ttf
          icon.icns
          icon.ico
          sfx/
          textures/
    test/
      java/
        AssetManagerIntegrationTest.java
        BlinkTest.java
        BrickTest.java
        GameEngineTest.java
        GameObjectTest.java
        MovableObjectTest.java
        PaddleTest.java
        ParticleSystemTest.java
        ParticleTest.java
        SoundManagerIntegrationTest.java
        SpriteTest.java
```

## Architecture and How Parts Connect

The project follows an MVC-style separation with explicit game-domain modules:

1. Application Entry
- `BrickBreakerGame` creates and switches scenes.
- It wires Menu, Game, and End screens through controllers.

2. Controllers (Input + Loop Orchestration)
- `MenuController`: menu interactions and transitions.
- `GameController`: owns the frame loop (`AnimationTimer`), forwards input to the model, triggers rendering.
- `EndGameController`: post-game UI and restart/menu flow.

3. Model (Game Rules and State)
- `GameEngine`: central coordinator for game state updates.
- `engine/CollisionHandler`: encapsulates collision resolution and score/effect triggers.
- `engine/EnemySpawner`: encapsulates enemy spawning cycles and movement timing.
- `Bricks/`, `PowerUps/`, and core entities contain domain behaviors.
- `effects/` holds visual-effect state objects (`Blink`, `Destroy`, `Particle`, `ParticleSystem`).
- `managers/` contains service-style singletons (`SoundManager`, `LeaderboardManager`).

4. View (Rendering)
- `GameView`, `MenuView`, `EndGameView` render to JavaFX canvas.
- `AssetManager` loads and serves textures/audio.

### Runtime Data Flow

1. User presses key in active scene.
2. Controller maps key to `InputSignal` and updates `GameEngine`.
3. `GameEngine.updateGame(deltaTime)` advances simulation.
4. `CollisionHandler` and `EnemySpawner` process specialized subsystems.
5. View reads current model state and renders frame.
6. On game end, controller transitions to End view and optional leaderboard update.

## OOP Principles Used

1. Encapsulation
- Game object internals are hidden behind methods and controlled access.
- State transitions are centralized in `GameEngine` and specialized engine classes.

2. Abstraction
- `GameObject` and `MovableObject` define reusable contracts for domain entities.
- Controllers and views operate on high-level model interfaces instead of internal details.

3. Inheritance
- Shared behavior is inherited across entities (for example movable vs non-movable game objects).
- Brick and power-up families share base behavior and override specifics.

4. Polymorphism
- Different brick/power-up/enemy types are processed through common parent types.
- Update and interaction logic delegates to concrete implementations at runtime.

5. Composition over Monolith
- Complex responsibilities are split into composable classes (`CollisionHandler`, `EnemySpawner`, managers, effects), reducing `GameEngine` size and coupling.

## Design Patterns in Use

- Singleton:
  - `GameEngine`
  - `AssetManager`
  - `SoundManager`
  - `LeaderboardManager`
- Factory:
  - `BrickFactory` builds concrete brick variants from type/configuration.

## Build and Run

### Prerequisites

- JDK 24 installed and available in `PATH`.

### Run the Game

```bash
./mvnw.cmd clean javafx:run
```

### Run Tests

```bash
./mvnw.cmd test
```

## Leaderboard Persistence

- File location: project root `leaderboard.txt`
- Format per line: `playerName,score,level`
- Managed by `LeaderboardManager`

## Notes

- The project is an educational implementation inspired by classic brick-breaker gameplay mechanics.
- Naming and packaging use `BrickBreakerFX` for project-safe branding.

## Credits

- This project is inspired by HanSolo's JArkanoid repository:
  - https://github.com/HanSolo/jarkanoid
- Some resources (sprites/audio ideas/assets) were adapted from that project for educational use in this school assignment.
- Original project author: Gerrit Grunwald (HanSolo).

## License and Legal

This project includes material inspired by and/or adapted from a third-party repository that is licensed under Apache License 2.0.

- Upstream project: HanSolo/jarkanoid
- Upstream license: Apache License 2.0
- Upstream license file: https://raw.githubusercontent.com/HanSolo/jarkanoid/main/LICENSE

To keep this repository compliant when distributing source or binaries:

1. Keep attribution to HanSolo/jarkanoid in this README and in `NOTICE`.
2. Keep a copy of Apache-2.0 license text in this repository (`THIRD_PARTY_LICENSES/Apache-2.0.txt`).
3. Keep modification notices where you changed/adapted upstream material.
4. Do not use the original project's trademarks or branding as your own product name.

Important:
- This section is practical compliance guidance for a school project, not legal advice.
- If your school publishes this project publicly, ask a teacher/supervisor to confirm your final licensing choice for your own original code.
