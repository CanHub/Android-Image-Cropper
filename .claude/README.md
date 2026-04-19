# Claude AI Development Environment

This directory contains AI agents and skills specifically designed for developing and maintaining the Android Image Cropper library.

## Directory Structure

```
.claude/
├── README.md          # This file
├── agents/            # Specialized AI agents for complex tasks
│   ├── test-writer.md
│   ├── code-reviewer.md
│   ├── bug-investigator.md
│   ├── dependency-updater.md
│   ├── api-designer.md
│   └── release-manager.md
└── skills/            # Quick-action skills for common tasks
    ├── feature-pipeline.md     # Complete feature workflow (Level 4)
    ├── bugfix-pipeline.md      # Complete bug fix workflow (Level 4)
    ├── run-tests.md
    ├── check-code-style.md
    ├── update-changelog.md
    ├── build-project.md
    └── add-translation.md
```

## Agents

Agents are specialized AI assistants for complex, multi-step tasks. They provide structured approaches and deep expertise in specific areas.

**Agent Levels**:
- **Level 2**: Simple specialized tasks (release prep, dependency updates)
- **Level 3**: Complex analysis and design (review, investigation, API design, testing)

**Key Principles**:
- **Role Boundaries**: Each agent has clear "responsible for" and "NOT responsible for" sections
- **Read-Only Analysis**: Design and review agents cannot Write/Edit (prevents self-approval)
- **Evidence-Based**: All findings cite `file:line` references
- **Success Criteria**: Explicit criteria define what constitutes successful output
- **Structured Output**: Standardized templates ensure consistency

### 🧪 Test Writer (`test-writer`)
**Purpose**: Write comprehensive tests with minimal mocking using real code.

**When to use**:
- Adding new features that need test coverage
- Writing regression tests for bugs
- Improving test coverage
- Creating API contract tests

**What it does**:
- Emphasizes real code over mocks
- Uses Robolectric for real Android components
- Creates integration tests as safeguards
- Tests actual behavior, not implementation
- Ensures tests survive version upgrades

**Example**:
```
"Write integration tests for the new crop window resize feature"
```

---

### 🔍 Code Reviewer (`code-reviewer`)
**Purpose**: Review code changes for quality, style, API compatibility, and security.

**When to use**:
- Before submitting a PR
- Reviewing a PR
- After making significant changes

**What it checks**:
- Code style compliance (ktlint)
- Code quality and best practices
- API compatibility and breaking changes
- Test coverage
- Security vulnerabilities
- Documentation completeness

**Example**:
```
"Review my changes to CropImageView for code quality and API compatibility"
```

---

### 🐛 Bug Investigator (`bug-investigator`)
**Purpose**: Systematically investigate and diagnose bugs.

**When to use**:
- Investigating bug reports
- Debugging failing tests
- Analyzing crash reports
- Finding root causes

**What it does**:
- Helps reproduce bugs
- Isolates the problem
- Identifies root causes
- Suggests fixes
- Documents findings

**Example**:
```
"Investigate why the crop window jumps on multi-touch gestures"
```

---

### 📦 Dependency Updater (`dependency-updater`)
**Purpose**: Safely update dependencies with proper testing.

**When to use**:
- Regular dependency maintenance
- Security updates
- Updating to new Android/Kotlin versions
- Renovate PR reviews

**What it does**:
- Analyzes dependency updates
- Checks compatibility
- Updates version catalogs
- Runs tests
- Documents changes

**Example**:
```
"Update Kotlin to 2.1.0 and test for compatibility"
```

---

### 🎨 API Designer (`api-designer`)
**Purpose**: Design new public APIs following best practices.

**When to use**:
- Adding new features
- Extending existing APIs
- Redesigning APIs
- Planning deprecations

**What it does**:
- Ensures API consistency
- Follows Kotlin best practices
- Maintains backward compatibility
- Provides design rationale
- Creates documentation

**Example**:
```
"Design an API for custom crop shapes beyond rectangle and oval"
```

## Skills

Skills are quick-action helpers for common, well-defined tasks. They provide commands and checklists.

**Skill Levels**:
- **Level 1**: Simple utilities (commands, checklists)
- **Level 4**: Multi-stage orchestration (pipelines coordinating agents)

### 🔄 Feature Pipeline (`feature-pipeline`) - Level 4
**Purpose**: Complete end-to-end workflow for adding new features.

**Pipeline Stages**:
1. **API Designer** - Design the API with alternatives
2. **Implementation** - Code the feature
3. **Test Writer** - Comprehensive test coverage
4. **Code Reviewer** - Quality gate
5. **Verification** - Final checks

**Usage**:
```
"Use feature-pipeline to add a resetZoom() method"
```

---

### 🐛 Bug Fix Pipeline (`bugfix-pipeline`) - Level 4
**Purpose**: Complete end-to-end workflow for investigating and fixing bugs.

**Pipeline Stages**:
1. **Bug Investigator** - Reproduce and diagnose
2. **Regression Test** - Write failing test
3. **Fix Implementation** - Fix the bug
4. **Verification** - Test + review
5. **Final Verification** - Full system check

**Usage**:
```
"Use bugfix-pipeline to fix issue #656"
```

---

### ✅ Run Tests (`run-tests`)
**Purpose**: Run the test suite with various configurations.

**Commands**:
```bash
# All tests with validation
./gradlew licensee ktlint testDebug build --stacktrace

# Just unit tests
./gradlew testDebug

# Paparazzi snapshot tests
./gradlew verifyPaparazziDebug
```

---

### 🎨 Check Code Style (`check-code-style`)
**Purpose**: Check and auto-fix code style with ktlint.

**Commands**:
```bash
# Auto-fix style issues
./gradlew ktlintFormat

# Check only (no fixes)
./gradlew ktlint
```

---

### 📝 Update CHANGELOG (`update-changelog`)
**Purpose**: Add entries to CHANGELOG.md following project conventions.

**Format**:
```markdown
- Category: Description [\#PR](url) ([username](url))
```

**Categories**: API, Fix, Security, Technical, Resource, Behavior Change

---

### 🌍 Add Translation (`add-translation`)
**Purpose**: Add new language translations or update existing ones.

**Process**:
1. Create `values-{lang}/strings.xml`
2. Translate all strings
3. Test in sample app
4. Update CHANGELOG.md

## Quick Reference

### Common Workflows

#### Adding a New Feature

**Option 1: Automated Pipeline** (Recommended)
```
🔄 Use feature-pipeline skill
```
Automatically orchestrates: API Designer → Implementation → Test Writer → Code Reviewer → Verification

**Option 2: Manual Steps**
1. 🎨 Use **API Designer** to design the API
2. Implement the feature
3. 🧪 Use **Test Writer** to add comprehensive tests
4. ✅ Use **Run Tests** to verify
5. 🎨 Use **Check Code Style** to format
6. 📝 Use **Update CHANGELOG** to document
7. 🔍 Use **Code Reviewer** before submitting PR

---

#### Fixing a Bug

**Option 1: Automated Pipeline** (Recommended)
```
🐛 Use bugfix-pipeline skill
```
Automatically orchestrates: Bug Investigator → Regression Test → Fix → Verification

**Option 2: Manual Steps**
1. 🐛 Use **Bug Investigator** to diagnose
2. 🧪 Use **Test Writer** to create failing regression test
3. Fix the bug
4. ✅ Use **Run Tests** to verify
5. 🎨 Use **Check Code Style** to format
6. 📝 Use **Update CHANGELOG** to document

---

#### Updating Dependencies
1. 📦 Use **Dependency Updater** for the update
2. ✅ Use **Run Tests** to verify compatibility
3. 📝 Use **Update CHANGELOG** to document

#### Adding Translation
1. 🌍 Use **Add Translation** skill
2. Test in sample app
3. 📝 Use **Update CHANGELOG** to document

## Best Practices

### When to Use Pipelines vs Agents vs Skills

**Use Pipeline Skills (Level 4) when**:
- Complete end-to-end workflow needed
- Multiple stages with handoffs
- Want automated orchestration
- Standard feature/bug workflow

**Use Agents (Level 2-3) when**:
- Single specialized task needed
- Need expert analysis without implementation
- Investigating or designing
- Want focused output (design, review, investigation)

**Use Utility Skills (Level 1) when**:
- Simple command execution
- Just need checklist/reference
- Quick routine task
- No orchestration needed

### Role Separation (Critical)

**NEVER mix these roles in same conversation**:
- ❌ Design + Implement + Review (same agent/context)
- ❌ Investigate + Fix + Approve (same agent/context)

**ALWAYS separate**:
- ✅ Designer designs, executor implements, reviewer approves
- ✅ Investigator diagnoses, tester writes test, fixer implements

**Why?** Self-approval introduces bias and misses issues.

### Working with Multiple Agents

You can use multiple agents in sequence:

```
1. "Use bug-investigator to diagnose issue #123"
2. [Fix the bug based on findings]
3. "Use code-reviewer to review my fix"
4. [Address feedback]
5. "Use update-changelog skill to document"
```

### Customizing Agents

Agents are markdown files and can be customized:
1. Edit the agent file in `.claude/agents/`
2. Modify checklists, processes, or examples
3. Save and use immediately

## Integration with CLAUDE.md

The main `CLAUDE.md` file (in project root) provides:
- Overall project context
- Tech stack information
- Build commands
- Development guidelines
- Code style rules

This `.claude/` directory provides:
- Specialized agents for specific tasks
- Quick-action skills
- Workflow guidance
- Task-specific best practices

Together, they provide comprehensive AI-assisted development support.

## Contributing New Agents/Skills

When adding new agents or skills:

### For Agents
1. Create `.claude/agents/your-agent.md`
2. Include frontmatter with name, description, model
3. Provide structured guidance and processes
4. Include examples and checklists
5. Update this README

### For Skills
1. Create `.claude/skills/your-skill.md`
2. Include frontmatter with name and description
3. Provide commands and usage examples
4. Include troubleshooting section
5. Update this README

## Maintenance

This directory should be maintained alongside the project:
- Update agents when processes change
- Add new skills for common tasks
- Keep examples current
- Update when tech stack changes
- Review and refine based on usage

## Maintenance

- **Agent/skill issues**: Update the relevant file directly
- **New agent ideas**: Add new agent/skill files following the established patterns

---

*These agents and skills help maintain the quality and consistency of Android Image Cropper.*
